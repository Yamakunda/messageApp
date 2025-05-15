package com.messager.messager.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.messager.messager.dto.MessageDTO.MessageDTO;
import com.messager.messager.dto.MessageDTO.request.MessageRequest;
import com.messager.messager.dto.MessageDTO.request.SeenRequest;
import com.messager.messager.dto.MessageDTO.request.TypingRequest;
import com.messager.messager.dto.MessageDTO.response.StatusResponse;
import com.messager.messager.model.Account;
import com.messager.messager.service.MessageService;

@RestController
@RequestMapping("/api")
public class MessageController {
    @Autowired
    private MessageService messageService;

    @PostMapping("/messages")
    public ResponseEntity<MessageDTO> sendMessage(
            @RequestBody MessageRequest request,
            @AuthenticationPrincipal Account account) {
        MessageDTO message = messageService.sendMessage(account.getId(), request.getRecipientId(), request.getContent());
        return ResponseEntity.status(201).body(message);
    }

    @PostMapping("/typing")
    public ResponseEntity<?> sendTyping(
            @RequestBody TypingRequest request,
            @AuthenticationPrincipal Account account) {
        messageService.sendTyping(account.getId(), request.getRecipientId(), request.isTyping());
        return ResponseEntity.ok().body(new StatusResponse("Typing event sent"));
    }

    @PostMapping("/messages/seen")
    public ResponseEntity<?> markSeen(
            @RequestBody SeenRequest request,
            @AuthenticationPrincipal Account account ) {
        messageService.markSeen(account.getId(), request.getMessageId());
        return ResponseEntity.ok().body(new StatusResponse("Message marked as seen"));
    }
}
