package com.example.whereshouldwego.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.bson.types.ObjectId;

import java.time.LocalDateTime;

@Document(collection = "chat_messages")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class ChatMessage {
    @Id
    private ObjectId id;

    private String roomCode;
    private Long userId;
    private String content;
    private LocalDateTime createdAt;
}
