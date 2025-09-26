package kz.nitec.sduchatbotapplication.service.implementation;

import kz.nitec.sduchatbotapplication.dto.*;
import kz.nitec.sduchatbotapplication.service.AssistantsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class AssistantsServiceImpl implements AssistantsService {

    @Qualifier("chatWebClient")
    private final WebClient client;

    @Override
    public Mono<CreateThreadResponse> createThread(String firstUserMessage) {
        var req = new CreateThreadRequest(
                firstUserMessage == null ? null :
                        java.util.List.of(new MessageItem("user", firstUserMessage))
        );
        return client.post()
                .uri(uriBuilder -> uriBuilder.path(path("threads")).build())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .retrieve()
                .bodyToMono(CreateThreadResponse.class);
    }

    @Override
    public Mono<CreateMessageResponse> addMessage(String threadId, String content) {
        var req = new CreateMessageRequest("user", content);
        return client.post()
                .uri(path("threads/" + threadId + "/messages"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .retrieve()
                .bodyToMono(CreateMessageResponse.class);
    }

    @Override
    public Mono<CreateRunResponse> createRun(String threadId, String assistantId) {
        var req = new CreateRunRequest(assistantId);
        return client.post()
                .uri(path("threads/" + threadId + "/runs"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(req)
                .retrieve()
                .bodyToMono(CreateRunResponse.class);
    }

    @Override
    public Mono<RunStatusResponse> getRun(String threadId, String runId) {
        return client.get()
                .uri(path("threads/" + threadId + "/runs/" + runId))
                .retrieve()
                .bodyToMono(RunStatusResponse.class);
    }

    @Override
    public Mono<Void> submitToolOutputs(String threadId, String runId, SubmitToolOutputsRequest body) {
        return client.post()
                .uri(path("threads/" + threadId + "/runs/" + runId + "/submit_tool_outputs"))
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Void.class);
    }

    @Override
    public Mono<ListMessagesResponse> listMessages(String threadId) {
        return client.get()
                .uri(path("threads/" + threadId + "/messages"))
                .retrieve()
                .bodyToMono(ListMessagesResponse.class);
    }

    private String path(String p) {
        return "/" + p.replaceAll("^/+", "");
    }
}
