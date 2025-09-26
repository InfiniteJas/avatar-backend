package kz.nitec.sduchatbotapplication.dto;

public record ChatRequest(
        String threadId,
        String message
) {}