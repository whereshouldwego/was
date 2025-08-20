package com.example.whereshouldwego.features.room.dto.response;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateRoomResponse {
    private String roomCode;
    private String roomUrl;

}
