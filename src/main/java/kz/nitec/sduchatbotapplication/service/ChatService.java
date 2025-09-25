package kz.nitec.sduchatbotapplication.service;

import kz.nitec.sduchatbotapplication.dto.ChatDto;
import kz.nitec.sduchatbotapplication.dto.MessageDto;
import kz.nitec.sduchatbotapplication.dto.ResponseMessageDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ChatService {

    ChatDto createChat(String firstMessage);

    ChatDto createChatViaAudio(MultipartFile firstAudio);

    ChatDto getChat(Long id);

    List<ChatDto> getChats();

    List<MessageDto> getChatMessages(Long id);

    ResponseMessageDto createMessage(Long id, String message);

    ResponseMessageDto createMessageViaAudio(Long id, MultipartFile firstAudio);
}
