package kz.nitec.sduchatbotapplication.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ResponseMessageDto {
    private MessageDto userMessage;
    private MessageDto responseMessage;
}
