package kz.nitec.sduchatbotapplication.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record SubmitToolOutputsRequest(
        @JsonProperty("tool_outputs") List<ToolOutput> toolOutputs
) {
    public record ToolOutput(
            @JsonProperty("tool_call_id") String toolCallId,
            String output
    ) {}
}
