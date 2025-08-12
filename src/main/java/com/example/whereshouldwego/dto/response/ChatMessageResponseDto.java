package com.example.whereshouldwego.dto.response;

import com.example.whereshouldwego.domain.ChatMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class ChatMessageResponseDto {
    private String id;
    private Long userId;
    private String username;
    private String roomCode;
    private String content;
    private LocalDateTime createdAt;

    public static ChatMessageResponseDto fromEntity(ChatMessage chatMessage) {
        return ChatMessageResponseDto.builder()
                .id(String.valueOf(chatMessage.getId()))
                .userId(chatMessage.getUserId())
                .username(chatMessage.getUsername())
                .roomCode(chatMessage.getRoomCode())
                .content(chatMessage.getContent())
                .createdAt(chatMessage.getCreatedAt())
                .build();
    }
}
