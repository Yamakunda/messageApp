package com.messager.messager.dto.MessageDTO.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TypingEvent {
    private Long senderId;
    private Long recipientId;
    private boolean isTyping;
}
