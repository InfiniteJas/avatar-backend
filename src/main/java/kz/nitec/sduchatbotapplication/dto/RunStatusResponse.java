package kz.nitec.sduchatbotapplication.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public record RunStatusResponse(
        String id,
        String status,
        @JsonProperty("required_action") RequiredAction requiredAction,
        @JsonProperty("last_error") Map<String, Object> lastError
) {
    public record RequiredAction(
            @JsonProperty("submit_tool_outputs") SubmitToolOutputs submitToolOutputs
    ) {
        public record SubmitToolOutputs(
                @JsonProperty("tool_calls") List<ToolCall> toolCalls
        ) {}
    }
}