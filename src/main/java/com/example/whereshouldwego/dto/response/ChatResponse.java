package com.example.whereshouldwego.dto.response;

import com.example.whereshouldwego.domain.Chat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class ChatResponse {
    private String id;
    private Long userId;
    private String username;
    private String roomCode;
    private String content;
    private LocalDateTime createdAt;

    public static ChatResponse fromEntity(Chat chat) {
        return ChatResponse.builder()
                .id(String.valueOf(chat.getId()))
                .userId(chat.getUserId())
                .username(chat.getUsername())
                .roomCode(chat.getRoomCode())
                .content(chat.getContent())
                .createdAt(chat.getCreatedAt())
                .build();
    }
}
