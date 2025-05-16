package com.messager.messager.dto.MessageDTO.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TypingRequest {
    private Long recipientId;
    private boolean isTyping;
}
