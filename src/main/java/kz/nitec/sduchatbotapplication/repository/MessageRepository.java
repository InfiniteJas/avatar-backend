package kz.nitec.sduchatbotapplication.repository;

import kz.nitec.sduchatbotapplication.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
    List<MessageEntity> findByChatIdOrderByIdAsc(Long chatId);
}
