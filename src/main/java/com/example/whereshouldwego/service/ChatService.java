package com.example.whereshouldwego.service;

import com.example.whereshouldwego.domain.ChatMessage;
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
    private final RabbitTemplate rabbitTemplate;
    private final ChatMessageRepository chatMessageRepository;

    public void publishToQueue(ChatMessageRequestDto dto) {
        rabbitTemplate.convertAndSend("chat.queue", dto);
    }

    public List<ChatMessageResponseDto> getChatHistory(String roomCode) {
        List<ChatMessage> messages = chatMessageRepository.findByRoomCodeOrderByCreatedAtAsc(roomCode);
        return messages.stream()
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
