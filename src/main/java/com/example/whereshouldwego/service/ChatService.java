package com.example.whereshouldwego.service;

import com.example.whereshouldwego.domain.Chat;
import com.example.whereshouldwego.dto.request.CandidateRequest;
import com.example.whereshouldwego.dto.request.ChatRequest;
import com.example.whereshouldwego.dto.response.ChatResponse;
import com.example.whereshouldwego.dto.response.CustomUserDetails;
import com.example.whereshouldwego.mapper.ChatMapper;
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
    private final ChatRepository chatRepository;

    public void handleAndBroadcast(ChatRequest req, CustomUserDetails user, String roomCode) {
        Chat chat = ChatMapper.toEntity(req, user.getId(), user.getUsername(), roomCode, LocalDateTime.now());
        Chat saved = chatRepository.save(chat);
        messagingTemplate.convertAndSend("/topic/chat." + roomCode, ChatResponse.from(saved));
    }

    public List<ChatResponse> getChatHistory(String roomCode) {
        return chatRepository.findByRoomCodeOrderByCreatedAtAsc(roomCode)
                .stream()
                .map(ChatResponse::from)
                .toList();
    }
}
