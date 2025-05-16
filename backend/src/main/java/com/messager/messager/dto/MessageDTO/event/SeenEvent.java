package com.messager.messager.dto.MessageDTO.event;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeenEvent {
    private UUID messageId;
    private Long recipientId;
    private String status;
    private LocalDateTime timestamp;
}
