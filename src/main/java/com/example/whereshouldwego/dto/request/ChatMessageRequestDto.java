package com.example.whereshouldwego.dto.request;

import com.example.whereshouldwego.domain.ChatMessage;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ChatMessageRequestDto {
    private Long userId;
    private String content;

    public ChatMessage toEntity() {
        return ChatMessage.builder()
                .userId(userId)
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
