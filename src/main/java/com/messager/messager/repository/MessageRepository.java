package com.messager.messager.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.messager.messager.model.Message;

import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, Long> {
    Optional<Message> findByMessageId(UUID messageId);
}
