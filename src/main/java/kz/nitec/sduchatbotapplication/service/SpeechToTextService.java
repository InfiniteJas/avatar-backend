package kz.nitec.sduchatbotapplication.service;

import org.springframework.web.multipart.MultipartFile;

public interface SpeechToTextService {
    public String speechToText(MultipartFile file);
}
