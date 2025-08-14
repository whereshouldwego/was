package com.example.whereshouldwego.service;

import com.example.whereshouldwego.domain.Chat;
import com.example.whereshouldwego.dto.request.ChatRequest;
import com.example.whereshouldwego.dto.response.ChatResponse;
import com.example.whereshouldwego.repository.mongo.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRepository chatMessageRepository;

    public void handleIncomingChat(ChatRequest dto, String roomCode) {
        Chat saved = chatMessageRepository.save(dto.toEntity(roomCode, LocalDateTime.now()));
        messagingTemplate.convertAndSend("/topic/chat." + roomCode, ChatResponse.fromEntity(saved));
    }

    public List<ChatResponse> getChatHistory(String roomCode) {
        return chatMessageRepository.findByRoomCodeOrderByCreatedAtAsc(roomCode)
                .stream()
                .map(ChatResponse::fromEntity)
                .toList();
    }
}
