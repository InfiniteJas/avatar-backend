package kz.nitec.sduchatbotapplication.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import kz.nitec.sduchatbotapplication.dto.ChatDto;
import kz.nitec.sduchatbotapplication.dto.MessageDto;
import kz.nitec.sduchatbotapplication.dto.ResponseMessageDto;
import kz.nitec.sduchatbotapplication.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/chats")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;

    @GetMapping("/{id}")
    ChatDto getChatById(@PathVariable Long id) {
        return chatService.getChat(id);
    }

    @PostMapping
    ChatDto createChat(@RequestBody String firstMessage){
        return chatService.createChat(firstMessage);
    }

    @PostMapping(value = "/audio", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ChatDto createChatViaAudio(@RequestBody MultipartFile firstAudio){
        return chatService.createChatViaAudio(firstAudio);
    }

    @GetMapping
    List<ChatDto> getChats(){
        return chatService.getChats();
    }

    @GetMapping("/{id}/messages")
    List<MessageDto> getMessages(@PathVariable Long id){
        return chatService.getChatMessages(id);
    }

    @PutMapping("/{id}/messages")
    ResponseMessageDto createMessage(@PathVariable Long id, @RequestBody String request){
        return chatService.createMessage(id, request);
    }

    @PutMapping(value = "/{id}/messages/audio", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    ResponseMessageDto createMessageViaAudio(@PathVariable Long id, @RequestBody MultipartFile audioRequest){
        return chatService.createMessageViaAudio(id, audioRequest);
    }
}
