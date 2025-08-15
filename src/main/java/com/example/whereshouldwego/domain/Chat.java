package com.example.whereshouldwego.domain;

import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@Document(collection = "chat_messages")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Chat {
    @Id
    private ObjectId id;
    private Long userId;
    private String username;
    private String roomCode;
    private String content;
    private LocalDateTime createdAt;

    public static Chat of(Long userId, String username, String roomCode, String content, LocalDateTime createdAt) {
        return Chat.builder()
                .userId(userId)
                .username(username)
                .roomCode(roomCode)
                .content(content)
                .createdAt(createdAt)
                .build();
    }
}
