package com.example.whereshouldwego.repository.mongo;

import com.example.whereshouldwego.domain.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    List<ChatMessage> findByRoomCodeOrderByCreatedAtAsc(String roomCode);
}
