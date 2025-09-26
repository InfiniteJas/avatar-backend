package kz.nitec.sduchatbotapplication.service.implementation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import kz.nitec.sduchatbotapplication.dto.*;
import kz.nitec.sduchatbotapplication.service.AssistantOrchestratorService;
import kz.nitec.sduchatbotapplication.service.AssistantsService;
import kz.nitec.sduchatbotapplication.service.ToolExecutorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Comparator;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AssistantOrchestratorServiceImpl implements AssistantOrchestratorService {

    private final AssistantsService client;

    private final ObjectMapper mapper;

    private final ToolExecutorService toolExecutor;


    @Value("${assistants.assistant-id}")
    private String assistantId;

    @Value("${assistants.poll.initial-delay-ms:800}")
    private long initialDelayMs;
    @Value("${assistants.poll.interval-ms:1500}")
    private long intervalMs;
    @Value("${assistants.poll.timeout-ms:60000}")
    private long timeoutMs;

    @Override
    public Mono<ChatResponse> sendAndWait(String maybeThreadId, String userMessage) {
        Mono<String> threadIdMono = (maybeThreadId == null || maybeThreadId.isBlank())
                ? client.createThread(null).map(r -> r.id())
                : Mono.just(maybeThreadId);

        return threadIdMono
                .flatMap(tid -> client.addMessage(tid, userMessage).thenReturn(tid))
                .flatMap(tid -> client.createRun(tid, assistantId).map(run -> new String[]{tid, run.id(), run.status()}))
                .flatMap(ids -> pollRunUntilComplete(ids[0], ids[1])
                        .then(fetchLatestAssistantMessage(ids[0], ids[1])
                                .map(answer -> new ChatResponse(ids[0], ids[1], "completed", answer))))
                .timeout(Duration.ofMillis(timeoutMs + 5000),
                        Mono.just(new ChatResponse(maybeThreadId, null, "timeout", "Ответ не получен вовремя.")));
    }

    /** Polling loop. Handles requires_action→submit_tool_outputs as pass-through no-op by default. */
    private Mono<Void> pollRunUntilComplete(String threadId, String runId) {
        return Mono.defer(() -> client.getRun(threadId, runId))
                .delaySubscription(Duration.ofMillis(initialDelayMs))
                .repeatWhenEmpty(r -> r.delayElements(Duration.ofMillis(intervalMs)))
                .expand(run -> {
                    var status = run.status();
                    switch (status) {
                        case "completed", "failed", "cancelled", "expired" -> {
                            return Mono.empty();
                        }
                        case "requires_action" -> {
                            var toolCalls = Optional.ofNullable(run.requiredAction())
                                    .map(RunStatusResponse.RequiredAction::submitToolOutputs)
                                    .map(RunStatusResponse.RequiredAction.SubmitToolOutputs::toolCalls)
                                    .orElse(java.util.List.of());

                            if (toolCalls.isEmpty()) {
                                return Mono.delay(Duration.ofMillis(intervalMs))
                                        .then(client.getRun(threadId, runId))
                                        .flux();
                            }

                            // ВЫПОЛНЯЕМ ВСЕ ФУНКЦИИ → СБОР ВЫХОДОВ → submit_tool_outputs
                            return Flux.fromIterable(toolCalls)
                                    .flatMap(tc -> {
                                        var req = new AssistantFunctionRequest(
                                                tc.function().name(),
                                                parseJsonSafe(tc.function().arguments())
                                        );
                                        return toolExecutor.execute(req)
                                                .map(resp -> new SubmitToolOutputsRequest.ToolOutput(
                                                        tc.id(),
                                                        resp.success() ? toJsonString(resp.result()) : toJsonString("{\"error\":\"" + resp.error() + "\"}")
                                                ))
                                                .onErrorResume(e ->
                                                        Mono.just(new SubmitToolOutputsRequest.ToolOutput(
                                                                tc.id(), toJsonString("{\"error\":\"" + e.getMessage() + "\"}")
                                                        )));
                                    })
                                    .collectList()
                                    .flatMap(outputs ->
                                            client.submitToolOutputs(threadId, runId,
                                                    new SubmitToolOutputsRequest(outputs)))
                                    .then(client.getRun(threadId, runId))
                                    .flux();
                        }
                        default -> {
                            return Mono.delay(Duration.ofMillis(intervalMs))
                                    .then(client.getRun(threadId, runId))
                                    .flux();
                        }
                    }
                })
                .takeUntil(r -> {
                    var s = r.status();
                    return "completed".equals(s) || "failed".equals(s) || "cancelled".equals(s) || "expired".equals(s);
                })
                .last()
                .flatMap(run -> {
                    if (!"completed".equals(run.status())) {
                        return Mono.error(new IllegalStateException("Run not completed: " + run.status()));
                    }
                    return Mono.empty();
                })
                .retryWhen(Retry.max(0)) // no automatic retries on overall failure; handled by timeout
                .then();
    }

    /** Pick the assistant message belonging to this run; fallback: latest assistant message. */
    private Mono<String> fetchLatestAssistantMessage(String threadId, String runId) {
        return client.listMessages(threadId)
                .map(ListMessagesResponse::data)
                .map(list -> {
                    var first = list.stream()
                            .filter(m -> "assistant".equals(m.role()) && runId.equals(m.runId()))
                            .max(Comparator.comparing(ListMessagesResponse.MessageData::id)) // or rely on API order
                            .orElseGet(() -> list.stream()
                                    .filter(m -> "assistant".equals(m.role()))
                                    .findFirst().orElse(null));
                    if (first == null || first.content() == null || first.content().isEmpty()) {
                        return "Пустой ответ ассистента.";
                    }
                    var c0 = first.content().get(0);
                    return (c0.text() != null && c0.text().value() != null) ? c0.text().value() : "Пустой ответ ассистента.";
                });
    }

    private String toJsonString(String s) {
        // ассистент ждёт строку; если это JSON-строка — отдаём как есть
        return s == null ? "" : s;
    }

    private JsonNode parseJsonSafe(String raw) {
        try { return mapper.readTree(raw == null ? "{}" : raw); }
        catch (Exception e) { return mapper.createObjectNode(); }
    }
}
