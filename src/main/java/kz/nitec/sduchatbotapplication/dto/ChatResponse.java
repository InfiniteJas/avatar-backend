package kz.nitec.sduchatbotapplication.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ChatResponse(
        String threadId,
        String runId,
        String status,
        String answer
) {}
