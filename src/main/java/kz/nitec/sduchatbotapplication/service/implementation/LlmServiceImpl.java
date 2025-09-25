//package kz.nitec.sduchatbotapplication.service.implementation;
//
//import kz.nitec.sduchatbotapplication.entity.MessageEntity;
//import kz.nitec.sduchatbotapplication.service.LlmService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//import org.springframework.web.multipart.MultipartFile;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//@Service
//@Slf4j
//@RequiredArgsConstructor
//public class LlmServiceImpl implements LlmService {
//
//    @Value("${llm.base-url}")
//    private String baseUrl;
//
//    @Value("${llm.request-timeout-seconds:120}")
//    private long timeoutSeconds;
//
//    private String model;
//
//    private WebClient client() {
//        return WebClient.builder()
//                .baseUrl(baseUrl)
//                .defaultHeader("Accept", MediaType.APPLICATION_JSON_VALUE)
//                .build();
//    }
//
//
//    @Override
//    public String completeOnce(List<MessageEntity> messages) {
//        var body = buildPayload(messages);
//
//
//        return "";
//    }
//
//    @Override
//    public String fromAudioToText(MultipartFile file) {
//        return "";
//    }
//
//    private Map<String, Object> buildPayload(List<MessageEntity> messages) {
//
//        List<Map<String, String>> msgs = new ArrayList<>();
//        for (MessageEntity m : messages) {
//            msgs.add(Map.of(
//                    "role", m.getRole().name().toLowerCase(),
//                    "content", m.getContent()
//            ));
//        }
//
//        Map<String, Object> payload = new HashMap<>();
//        payload.put("model", model);
//        payload.put("messages", msgs);
//        payload.put("stream", false);
//        return payload;
//    }
//}
