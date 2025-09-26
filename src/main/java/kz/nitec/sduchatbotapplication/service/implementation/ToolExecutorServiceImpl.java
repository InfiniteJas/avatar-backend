package kz.nitec.sduchatbotapplication.service.implementation;

import com.fasterxml.jackson.databind.JsonNode;
import kz.nitec.sduchatbotapplication.dto.AssistantFunctionRequest;
import kz.nitec.sduchatbotapplication.dto.AssistantFunctionResponse;
import kz.nitec.sduchatbotapplication.service.ToolExecutorService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.StringJoiner;

@Service
@RequiredArgsConstructor
public class ToolExecutorServiceImpl implements ToolExecutorService {

    // SERPAPI
    @Value("${assistants.tools.websearch.provider:serpapi}")
    private String searchProvider;
    @Value("${assistants.tools.websearch.serpapi.api-key:changeme}")
    private String serpApiKey;
    @Value("${assistants.tools.websearch.serpapi.hl:ru}")
    private String serpHl;
    @Value("${assistants.tools.websearch.serpapi.gl:ru}")
    private String serpGl;
    @Value("${assistants.tools.websearch.serpapi.results:5}")
    private int serpResults;

    // Wren AI
    @Value("${assistants.tools.wren.url}")
    private String wrenUrl;
    @Value("${assistants.tools.wren.token}")
    private String wrenToken;
    @Value("${assistants.tools.wren.project-id}")
    private Integer wrenProjectId;

    // External model
    @Value("${assistants.tools.external.url}")
    private String externalUrl;
    @Value("${assistants.tools.external.token}")
    private String externalToken;

    @Qualifier("defaultWebClient")
    private final WebClient http;

    @Override
    public Mono<AssistantFunctionResponse> execute(AssistantFunctionRequest req) {
        String name = req.function_name();
        JsonNode args = req.arguments();

        if ("perform_web_search".equals(name)) {
            return performWebSearch(args);
        } else if ("db_query".equals(name)) {
            return dbQuery(args);
        } else if ("get_external_info".equals(name)) {
            return getExternalInfo(args);
        }
        return Mono.just(AssistantFunctionResponse.fail("Unknown function: " + name));
    }


    /** perform_web_search(search_query: string) */
    private Mono<AssistantFunctionResponse> performWebSearch(JsonNode args) {
        String query = safeText(args, "search_query");
        if (query == null || query.isBlank()) {
            return Mono.just(AssistantFunctionResponse.fail("search_query is required"));
        }
        if (!"serpapi".equalsIgnoreCase(searchProvider)) {
            return Mono.just(AssistantFunctionResponse.fail("Only serpapi is wired"));
        }

        String url = "https://serpapi.com/search.json";
        return http.get()
                .uri(uriBuilder -> uriBuilder.path(url)
                        .queryParam("engine", "google")
                        .queryParam("q", query)
                        .queryParam("num", serpResults)
                        .queryParam("hl", serpHl)
                        .queryParam("gl", serpGl)
                        .queryParam("api_key", serpApiKey)
                        .build())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(this::formatSerpApiResults)
                .map(AssistantFunctionResponse::ok)
                .onErrorResume(e -> Mono.just(AssistantFunctionResponse.fail("web_search failed: " + e.getMessage())));
    }

    private String formatSerpApiResults(JsonNode data) {
        var arr = data.path("organic_results");
        if (!arr.isArray() || arr.isEmpty()) return "По вашему запросу ничего не найдено.";
        var sj = new StringJoiner("\n\n");
        int i = 1;
        for (JsonNode r : arr) {
            String title = textOrEmpty(r, "title");
            String link  = textOrEmpty(r, "link");
            String snip  = textOrEmpty(r, "snippet");
            sj.add("Источник " + (i++) + ":\n" +
                    "Заголовок: " + title + "\n" +
                    "URL: " + link + "\n" +
                    "Фрагмент: " + (snip.length() > 400 ? snip.substring(0,400) : snip));
            if (i > serpResults) break;
        }
        return sj.toString();
    }

    /** db_query(message: string) -> Wren AI */
    private Mono<AssistantFunctionResponse> dbQuery(JsonNode args) {
        String message = safeText(args, "message");
        if (message == null || message.isBlank()) {
            return Mono.just(AssistantFunctionResponse.fail("message is required"));
        }
        var body = """
                { "projectId": %d, "question": "%s" }
                """.formatted(wrenProjectId, escapeJson(message));

        return http.post()
                .uri(wrenUrl)
                .header("Authorization", "Bearer " + wrenToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(json -> {
                    String summary = textOrEmpty(json, "summary");
                    if (summary.isBlank()) summary = "Данные не найдены";
                    return AssistantFunctionResponse.ok(summary);
                })
                .onErrorResume(e -> Mono.just(AssistantFunctionResponse.fail("db_query failed: " + e.getMessage())));
    }

    /** get_external_info(source_model: string, user_query: string) */
    private Mono<AssistantFunctionResponse> getExternalInfo(JsonNode args) {
        String model = safeText(args, "source_model");
        String userQuery = safeText(args, "user_query");
        if (userQuery == null || userQuery.isBlank()) {
            return Mono.just(AssistantFunctionResponse.fail("user_query is required"));
        }

        String body = """
          { "model": "%s", "stream": false,
            "messages": [ { "role": "user", "content": "%s" } ]
          }""".formatted(escapeJson(nullToEmpty(model)), escapeJson(userQuery));

        return http.post()
                .uri(externalUrl)
                .header("Authorization", "Bearer " + externalToken)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(json -> {
                    var choices = json.path("choices");
                    String content = choices.isArray() && !choices.isEmpty()
                            ? choices.get(0).path("message").path("content").asText("")
                            : "";
                    return AssistantFunctionResponse.ok(content);
                })
                .onErrorResume(e -> Mono.just(AssistantFunctionResponse.fail("external_info failed: " + e.getMessage())));
    }

    private static String safeText(JsonNode node, String field) {
        return node != null && node.has(field) && !node.get(field).isNull()
                ? node.get(field).asText(null) : null;
    }
    private static String textOrEmpty(JsonNode node, String field) {
        return node.has(field) && !node.get(field).isNull()
                ? node.get(field).asText("") : "";
    }
    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\","\\\\").replace("\"","\\\"").replace("\n","\\n").replace("\r","");
    }
    private static String nullToEmpty(String s) { return s == null ? "" : s; }
}
