package com.messager.messager.service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.messager.messager.dto.MessageDTO.MessageDTO;
import com.messager.messager.dto.MessageDTO.event.SeenEvent;
import com.messager.messager.dto.MessageDTO.event.TypingEvent;
import com.messager.messager.model.Message;
import com.messager.messager.repository.MessageRepository;

@Service
public class MessageServiceImp implements MessageService {
    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Override
    public MessageDTO sendMessage(Long senderId, Long recipientId, String content) {
        Message message = new Message();
        message.setMessageId(UUID.randomUUID());
        message.setSenderId(senderId);
        message.setRecipientId(recipientId);
        message.setContent(content);
        message.setTimestamp(LocalDateTime.now());
        message.setStatus("sent");

        messageRepository.save(message);

        MessageDTO dto = convertToDTO(message);
        messagingTemplate.convertAndSendToUser(
            recipientId.toString(),
            "/queue/messages",
            dto
        );

        return dto;
    }
    @Override
    public void sendTyping(Long senderId, Long recipientId, boolean isTyping) {
        messagingTemplate.convertAndSendToUser(
            recipientId.toString(),
            "/queue/typing",
            new TypingEvent(senderId, recipientId, isTyping)
        );
    }
    @Override
    public void markSeen(Long userId, UUID messageId) {
        Message message = messageRepository.findByMessageId(messageId)
            .orElseThrow(() -> new RuntimeException("Message not found"));
        if (!message.getRecipientId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        message.setStatus("seen");
        messageRepository.save(message);

        messagingTemplate.convertAndSendToUser(
            message.getSenderId().toString(),
            "/queue/seen",
            new SeenEvent(messageId, userId, "seen", LocalDateTime.now())
        );
    }

    @Override
    public List<MessageDTO> getMessageHistory(Long id, Long accountId) {
        List<Message> messages = messageRepository.findChatHistory(id, accountId);
        return messages.stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    private MessageDTO convertToDTO(Message message) {
        MessageDTO dto = new MessageDTO();
        dto.setMessageId(message.getMessageId());
        dto.setSenderId(message.getSenderId());
        dto.setRecipientId(message.getRecipientId());
        dto.setContent(message.getContent());
        dto.setTimestamp(message.getTimestamp());
        dto.setStatus(message.getStatus());
        return dto;
    }
}
