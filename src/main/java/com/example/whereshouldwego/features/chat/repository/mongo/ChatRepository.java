package com.example.whereshouldwego.features.chat.repository.mongo;

import com.example.whereshouldwego.features.chat.domain.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatRepository extends MongoRepository<Chat, String> {
    List<Chat> findByRoomCodeOrderByCreatedAtAsc(String roomCode);
}
