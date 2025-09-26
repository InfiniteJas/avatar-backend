package kz.nitec.sduchatbotapplication.service;

import kz.nitec.sduchatbotapplication.dto.*;
import reactor.core.publisher.Mono;

public interface AssistantsService {
    Mono<CreateThreadResponse> createThread(String firstUserMessage);

    Mono<CreateMessageResponse> addMessage(String threadId, String content);

    Mono<CreateRunResponse> createRun(String threadId, String assistantId);

    Mono<RunStatusResponse> getRun(String threadId, String runId);

    Mono<Void> submitToolOutputs(String threadId, String runId, SubmitToolOutputsRequest body);

    Mono<ListMessagesResponse> listMessages(String threadId);
}
