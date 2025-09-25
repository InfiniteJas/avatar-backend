package kz.nitec.sduchatbotapplication.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;

@Entity
@Table(name = "messages")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class MessageEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "chat_id")
    private ChatEntity chat;

    @Enumerated(EnumType.STRING)
    private Role role;  // USER or ASSISTANT or SYSTEM

    @Column(length = 8000)
    private String content;

    private OffsetDateTime createdAt = OffsetDateTime.now();

    public enum Role { USER, ASSISTANT, SYSTEM }
}
