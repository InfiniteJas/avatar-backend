package kz.nitec.sduchatbotapplication.entity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Formula;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "chats")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ChatEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @CreationTimestamp
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    private OffsetDateTime updatedAt;

    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("id DESC ")
    private List<MessageEntity> messages = new ArrayList<>();

    @Transient
    public void addMessage(String content, MessageEntity.Role role) {
        var m = new MessageEntity();
        m.setContent(content);
        m.setRole(role);
        m.setChat(this);
        messages.add(m);
    }

    @Transient
    public List<MessageEntity> getLastMessages(int limit) {
        return this.messages.stream().limit(limit).toList();
    }
}
