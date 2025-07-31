package com.example.whereshouldwego.dto.request;

import lombok.Getter;

@Getter
public class ChatMessageRequestDto {
    private String roomCode;
    private Long userId;
    private String content;
}
