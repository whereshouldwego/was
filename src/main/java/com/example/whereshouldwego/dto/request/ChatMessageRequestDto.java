package com.example.whereshouldwego.dto.request;

import com.example.whereshouldwego.domain.ChatMessage;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ChatMessageRequestDto {
    private Long userId;
    private String username;
    private String content;

    public ChatMessage toEntity(String roomCode) {
        return ChatMessage.builder()
                .userId(userId)
                .username(username)
                .roomCode(roomCode)
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
