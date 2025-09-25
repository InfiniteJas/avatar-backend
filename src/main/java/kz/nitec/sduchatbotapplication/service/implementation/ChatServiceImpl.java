package kz.nitec.sduchatbotapplication.service.implementation;

import kz.nitec.sduchatbotapplication.dto.ChatDto;
import kz.nitec.sduchatbotapplication.dto.MessageDto;
import kz.nitec.sduchatbotapplication.dto.ResponseMessageDto;
import kz.nitec.sduchatbotapplication.entity.ChatEntity;
import kz.nitec.sduchatbotapplication.entity.MessageEntity;
import kz.nitec.sduchatbotapplication.mapper.ChatMapper;
import kz.nitec.sduchatbotapplication.mapper.MessageMapper;
import kz.nitec.sduchatbotapplication.repository.ChatRepository;
import kz.nitec.sduchatbotapplication.repository.MessageRepository;
import kz.nitec.sduchatbotapplication.service.ChatService;
import kz.nitec.sduchatbotapplication.service.SpeechToTextService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static kz.nitec.sduchatbotapplication.entity.MessageEntity.Role.ASSISTANT;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final ChatRepository chatRepository;
    private final MessageRepository messageRepository;

    private final SpeechToTextService speechToTextService;

    @Transactional
    @Override
    public ChatDto createChat(String firstMessage) {
        var chat = new ChatEntity();
        chat.setTitle(firstMessage);

        chat.addMessage(firstMessage, MessageEntity.Role.USER);

        getResponse(chat);

        chat = chatRepository.save(chat);
        return ChatMapper.INSTANCE.toDto(chat);
    }

    @Transactional
    @Override
    public ChatDto createChatViaAudio(MultipartFile firstAudio) {
        var text = speechToTextService.speechToText(firstAudio);
        return createChat(text);
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
        chat.addMessage(message, MessageEntity.Role.USER);
        getResponse(chat);
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
        var text = speechToTextService.speechToText(firstAudio);
        return createMessage(id, text);
    }

    private void getResponse(ChatEntity chatEntity) {
        chatEntity.addMessage("заглушка", ASSISTANT);
    }

    private ChatEntity getEntityById(Long id) {
        return chatRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }
}
