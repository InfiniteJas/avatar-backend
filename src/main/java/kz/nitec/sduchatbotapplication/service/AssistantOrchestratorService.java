package kz.nitec.sduchatbotapplication.service;

import kz.nitec.sduchatbotapplication.dto.ChatResponse;
import reactor.core.publisher.Mono;

public interface AssistantOrchestratorService {
    Mono<ChatResponse> sendAndWait(String maybeThreadId, String userMessage);
}
