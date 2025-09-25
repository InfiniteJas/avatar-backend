package kz.nitec.sduchatbotapplication.repository;

import kz.nitec.sduchatbotapplication.entity.ChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRepository extends JpaRepository<ChatEntity, Long> {
}
