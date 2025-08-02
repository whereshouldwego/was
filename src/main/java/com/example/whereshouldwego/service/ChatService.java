package com.example.whereshouldwego.service;

import com.example.whereshouldwego.dto.request.ChatMessageRequestDto;
import com.example.whereshouldwego.dto.response.ChatMessageResponseDto;
import com.example.whereshouldwego.repository.mongo.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private static final String CHAT_QUEUE = "chat.queue";

    private final RabbitTemplate rabbitTemplate;
    private final ChatMessageRepository chatMessageRepository;

    public void sendChatMessageToQueue(ChatMessageRequestDto dto) {
        rabbitTemplate.convertAndSend(CHAT_QUEUE, dto);
    }

    public List<ChatMessageResponseDto> getChatHistory(String roomCode) {
        return chatMessageRepository.findByRoomCodeOrderByCreatedAtAsc(roomCode)
                .stream()
                .map(msg -> ChatMessageResponseDto.builder()
                        .id(msg.getId() != null ? msg.getId().toHexString() : null)
                        .roomCode(msg.getRoomCode())
                        .userId(msg.getUserId())
                        .content(msg.getContent())
                        .createdAt(msg.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }
}
