package com.example.whereshouldwego.dto;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateRoomResponse {
    private String roomCode;
    private String roomUrl;

}
