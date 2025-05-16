package com.messager.messager.dto.MessageDTO;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageDTO {
    private UUID messageId;
    private Long senderId;
    private Long recipientId;
    private String content;
    private LocalDateTime timestamp;
    private String status;
}
