package com.example.whereshouldwego.repository.mongo;

import com.example.whereshouldwego.domain.Chat;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatRepository extends MongoRepository<Chat, String> {
    List<Chat> findByRoomCodeOrderByCreatedAtAsc(String roomCode);
}
