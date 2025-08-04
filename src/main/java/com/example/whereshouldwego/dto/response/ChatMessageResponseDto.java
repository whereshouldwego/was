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
    private String roomCode;
    private Long userId;
    private String content;
    private LocalDateTime createdAt;

    public static ChatMessageResponseDto fromEntity(ChatMessage chatMessage) {
        return ChatMessageResponseDto.builder()
                .id(chatMessage.getId() != null ? chatMessage.getId().toString() : null)
                .roomCode(chatMessage.getRoomCode())
                .userId(chatMessage.getUserId())
                .content(chatMessage.getContent())
                .createdAt(chatMessage.getCreatedAt())
                .build();
    }
}
