package com.example.whereshouldwego.consumer;

import com.example.whereshouldwego.domain.ChatMessage;
import com.example.whereshouldwego.dto.request.ChatMessageRequestDto;
import com.example.whereshouldwego.dto.response.ChatMessageResponseDto;
import com.example.whereshouldwego.repository.mongo.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class ChatConsumer {

    private final ChatMessageRepository chatMessageRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @RabbitListener(queues = "chat.queue")
    public void consume(ChatMessageRequestDto dto) {
        ChatMessage saved = chatMessageRepository.save(ChatMessage.builder()
                .roomCode(dto.getRoomCode())
                .userId(dto.getUserId())
                .content(dto.getContent())
                .createdAt(LocalDateTime.now())
                .build());

        ChatMessageResponseDto response = ChatMessageResponseDto.builder()
                .id(saved.getId() != null ? saved.getId().toHexString() : null)
                .roomCode(saved.getRoomCode())
                .userId(saved.getUserId())
                .content(saved.getContent())
                .createdAt(saved.getCreatedAt())
                .build();

        System.out.println("Sending to: /topic/chat/" + response.getRoomCode());
        System.out.println("Payload: " + response.getContent());

        messagingTemplate.convertAndSend("/topic/chat/" + saved.getRoomCode(), response);
    }
}