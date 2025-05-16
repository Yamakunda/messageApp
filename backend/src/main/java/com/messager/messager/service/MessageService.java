package com.messager.messager.service;

import java.util.List;
import java.util.UUID;

import com.messager.messager.dto.MessageDTO.MessageDTO;

public interface MessageService {
  MessageDTO sendMessage(Long senderId, Long recipientId, String content);  
  void sendTyping(Long senderId, Long recipientId, boolean isTyping);
  void markSeen(Long userId, UUID messageId);
  List<MessageDTO> getMessageHistory(Long id, Long accountId);
}
