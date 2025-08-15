package com.example.whereshouldwego.service;

import com.example.whereshouldwego.domain.Chat;
import com.example.whereshouldwego.dto.request.ChatRequest;
import com.example.whereshouldwego.dto.response.ChatResponse;
import com.example.whereshouldwego.dto.response.CustomUserDetails;
import com.example.whereshouldwego.repository.mongo.ChatRepository;
import com.example.whereshouldwego.repository.postgres.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRepository chatRepository;
    private final UserRepository userRepository;

    public void handleAndBroadcast(ChatRequest request, CustomUserDetails user, String roomCode) {
        Long userId = userRepository.findIdByUsername(user.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        Chat chat = Chat.of(userId, user.getUsername(), roomCode, request.getContent(), LocalDateTime.now());
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
