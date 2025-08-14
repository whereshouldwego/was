package com.example.whereshouldwego.dto.request;

import com.example.whereshouldwego.domain.Chat;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ChatRequest {
    private Long userId;
    private String username;
    private String content;

    public Chat toEntity(String roomCode, LocalDateTime createdAt) {
        return Chat.builder()
                .userId(userId)
                .username(username)
                .roomCode(roomCode)
                .content(content)
                .createdAt(createdAt)
                .build();
    }
}
