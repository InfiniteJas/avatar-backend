package kz.nitec.sduchatbotapplication.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public record AssistantFunctionResponse(
        boolean success,
        String result,
        String error
) {
    public static AssistantFunctionResponse ok(String result) {
        return new AssistantFunctionResponse(true, result, null);
    }
    public static AssistantFunctionResponse fail(String error) {
        return new AssistantFunctionResponse(false, null, error);
    }
}
