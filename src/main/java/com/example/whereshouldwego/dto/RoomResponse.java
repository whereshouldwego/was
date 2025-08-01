package com.example.whereshouldwego.dto;

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
