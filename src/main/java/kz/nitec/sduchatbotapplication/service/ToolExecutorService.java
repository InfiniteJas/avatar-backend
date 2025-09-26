package kz.nitec.sduchatbotapplication.service;

import kz.nitec.sduchatbotapplication.dto.AssistantFunctionRequest;
import kz.nitec.sduchatbotapplication.dto.AssistantFunctionResponse;
import reactor.core.publisher.Mono;

public interface ToolExecutorService {
    Mono<AssistantFunctionResponse> execute(AssistantFunctionRequest req);
}
