package com.example.whereshouldwego.features.room.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RoomResponse {
    private String roomCode;
    private String roomUrl;
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;
}
