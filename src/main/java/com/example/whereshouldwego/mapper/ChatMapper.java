package com.example.whereshouldwego.mapper;

import com.example.whereshouldwego.domain.Chat;
import com.example.whereshouldwego.dto.request.ChatRequest;
import com.example.whereshouldwego.dto.response.ChatResponse;

import java.time.LocalDateTime;

public final class ChatMapper {
    public static Chat toEntity(ChatRequest req, Long userId, String username, String roomCode, LocalDateTime createdAt) {
        return Chat.of(userId, username, roomCode, req.getContent(), createdAt);
    }

    public static ChatResponse toResponse(Chat chat) {
        return ChatResponse.from(chat);
    }
}