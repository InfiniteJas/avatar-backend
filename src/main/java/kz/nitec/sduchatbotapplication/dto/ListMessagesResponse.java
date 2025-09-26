package kz.nitec.sduchatbotapplication.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record ListMessagesResponse(List<MessageData> data) {
    public record MessageData(
            String id,
            String role,
            @JsonProperty("run_id") String runId,
            List<MessageContent> content
    ) {}
}