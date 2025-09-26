package kz.nitec.sduchatbotapplication.dto;

import lombok.*;

import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChatDto {
    private Long Id;
    private String title;
    private String threadId;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
