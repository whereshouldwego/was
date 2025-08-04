package com.example.whereshouldwego.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class JoinRoomResponse {
    private Long userId;
    private String roomCode;
}
