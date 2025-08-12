package com.example.whereshouldwego.service;

import com.example.whereshouldwego.domain.ChatMessage;
import com.example.whereshouldwego.dto.request.ChatMessageRequestDto;
import com.example.whereshouldwego.dto.response.ChatMessageResponseDto;
import com.example.whereshouldwego.repository.mongo.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatMessageRepository chatMessageRepository;

    public void handleIncomingChat(ChatMessageRequestDto dto, String roomCode) {
        ChatMessage saved = chatMessageRepository.save(dto.toEntity(roomCode));

        ChatMessageResponseDto response = ChatMessageResponseDto.fromEntity(saved);

        messagingTemplate.convertAndSend("/topic/chat." + roomCode, response);
    }

    public List<ChatMessageResponseDto> getChatHistory(String roomCode) {
        return chatMessageRepository.findByRoomCodeOrderByCreatedAtAsc(roomCode)
                .stream()
                .map(ChatMessageResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
}
