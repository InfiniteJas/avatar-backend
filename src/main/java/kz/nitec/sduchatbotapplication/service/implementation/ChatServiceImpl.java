package kz.nitec.sduchatbotapplication.service.implementation;

import kz.nitec.sduchatbotapplication.dto.ChatDto;
import kz.nitec.sduchatbotapplication.dto.ChatResponse;
import kz.nitec.sduchatbotapplication.dto.MessageDto;
import kz.nitec.sduchatbotapplication.dto.ResponseMessageDto;
import kz.nitec.sduchatbotapplication.entity.ChatEntity;
import kz.nitec.sduchatbotapplication.entity.MessageEntity;
import kz.nitec.sduchatbotapplication.mapper.ChatMapper;
import kz.nitec.sduchatbotapplication.mapper.MessageMapper;
import kz.nitec.sduchatbotapplication.repository.ChatRepository;
import kz.nitec.sduchatbotapplication.repository.MessageRepository;
import kz.nitec.sduchatbotapplication.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.View;

import java.time.Duration;
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;

    private final AssistantsService service;
    private final AssistantOrchestratorService orchestratorService;
    private final View error;

    @Transactional
    @Override
    public ChatDto createChat(String firstMessage) {

        var thread = service.createThread(firstMessage)
                .doOnError(e -> log.error("Failed to create thread", e))
                .blockOptional(Duration.ofSeconds(15))
                .orElseThrow(() -> new IllegalStateException("Assistant thread was not created"));

        var threadId = thread.id();

        var chat = new ChatEntity();
        chat.setTitle(firstMessage);
        chat.setThreadId(threadId);

        var answer = orchestratorService.sendAndWait(threadId, firstMessage)
                .doOnError(e -> log.error("Failed to get answer", e))
                .blockOptional(Duration.ofSeconds(60))
                .orElseThrow(() -> new IllegalStateException("Dont got answer message!"));

        chat.addUserMessage(firstMessage, answer.runId());
        chat.addResponseMessage(answer.answer(), answer.runId());
        chat = chatRepository.save(chat);
        return ChatMapper.INSTANCE.toDto(chat);
    }

    @Transactional
    @Override
    public ChatDto createChatViaAudio(MultipartFile firstAudio) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
    }

    @Transactional(readOnly = true)
    @Override
    public ChatDto getChat(Long id) {
        var chat = getEntityById(id);
        return ChatMapper.INSTANCE.toDto(chat);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ChatDto> getChats() {
        return chatRepository.findAll().stream().map(ChatMapper.INSTANCE::toDto).toList();
    }

    @Transactional(readOnly = true)
    @Override
    public List<MessageDto> getChatMessages(Long id) {
        return messageRepository.findByChatIdOrderByIdAsc(id).stream().map(MessageMapper.INSTANCE::toDto).toList();
    }

    @Transactional
    @Override
    public ResponseMessageDto createMessage(Long id, String message) {
        var chat = getEntityById(id);

        if(chat.getThreadId() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Please register new chat!");
        }

        var answer = orchestratorService.sendAndWait(chat.getThreadId(), message)
                .doOnError(e -> log.error("Failed to get answer", e))
                .blockOptional(Duration.ofSeconds(60))
                .orElseThrow(() -> new IllegalStateException("Dont got answer message!"));

        chat.addUserMessage(message, answer.runId());
        chat.addResponseMessage(answer.answer(), answer.runId());

        chat = chatRepository.save(chat);
        var result = chat.getLastMessages(2);
        var response =  new ResponseMessageDto();
        response.setUserMessage(MessageMapper.INSTANCE.toDto(result.getLast()));
        response.setResponseMessage(MessageMapper.INSTANCE.toDto(result.getFirst()));
        return response;
    }

    @Transactional
    @Override
    public ResponseMessageDto createMessageViaAudio(Long id, MultipartFile firstAudio) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST);

    }


    private ChatEntity getEntityById(Long id) {
        return chatRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
