package com.example.whereshouldwego.features.chat.service;

import com.example.whereshouldwego.features.chat.domain.Chat;
import com.example.whereshouldwego.features.chat.dto.request.ChatRequest;
import com.example.whereshouldwego.features.chat.dto.response.ChatResponse;
import com.example.whereshouldwego.features.chat.repository.mongo.ChatRepository;
import com.example.whereshouldwego.features.room.repository.RoomParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import static com.example.whereshouldwego.common.util.RoomCodeUtil.decode;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ChatRepository chatRepository;
    private final RoomParticipantRepository roomParticipantRepository;

    public void handleAndBroadcast(ChatRequest request, Long userId, String roomCode) {
        Long roomId = decode(roomCode);

        if (!roomParticipantRepository.existsByRoomIdAndUserId(roomId, userId)) {
            throw new AccessDeniedException("User is not a member of the room");
        }

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
