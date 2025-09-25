package kz.nitec.sduchatbotapplication.dto;

import kz.nitec.sduchatbotapplication.entity.MessageEntity;
import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MessageDto {
    private Long id;
    private String content;

    private MessageEntity.Role role;
    private OffsetDateTime createdAt;
}
