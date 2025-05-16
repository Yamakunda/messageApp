package com.messager.messager.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.messager.messager.model.Message;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, Long> {
    Optional<Message> findByMessageId(UUID messageId);
    List<Message> findBySenderIdAndRecipientId(Long senderId, Long recipientId);
    
    @Query("SELECT m FROM Message m WHERE " +
           "(m.senderId = :userId AND m.recipientId = :recipientId) OR " +
           "(m.senderId = :recipientId AND m.recipientId = :userId) " +
           "ORDER BY m.timestamp ASC")
    List<Message> findChatHistory(Long userId, Long recipientId);
}
