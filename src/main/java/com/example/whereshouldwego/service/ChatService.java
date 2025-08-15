package com.example.whereshouldwego.service;

import com.example.whereshouldwego.domain.Chat;
import com.example.whereshouldwego.dto.request.ChatRequest;
import com.example.whereshouldwego.dto.response.ChatResponse;
import com.example.whereshouldwego.dto.response.CustomUserDetails;
import com.example.whereshouldwego.repository.mongo.ChatRepository;
import com.example.whereshouldwego.repository.postgres.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
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

    public void handleAndBroadcast(ChatRequest request, Authentication authentication, String roomCode) {
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        String jwtUsername = principal.getUsername();

        Long userId = userRepository.findIdByUsername(jwtUsername)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + jwtUsername));

        Chat chat = Chat.of(userId, request.getUsername(), roomCode, request.getContent(), LocalDateTime.now());
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
