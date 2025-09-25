package kz.nitec.sduchatbotapplication.service.implementation;

import kz.nitec.sduchatbotapplication.service.SpeechToTextService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class SpeechToTextServiceImpl implements SpeechToTextService {

    @Value("${azure.speech.key}")
    private String key;

    @Value("${azure.speech.region}")
    private String region;

    @Value("${azure.speech.private-endpoint-enabled}")
    private boolean privateEndpointEnabled;

    @Value("${azure.speech.private-endpoint}")
    private String privateEndpoint;

    @Value("${azure.speech.locales}")
    private String localesCsv;

    @Override
    public String speechToText(MultipartFile file) {
        return "Загрушка";
    }


}
