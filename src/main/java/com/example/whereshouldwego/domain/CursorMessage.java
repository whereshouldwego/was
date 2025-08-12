package com.example.whereshouldwego.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CursorMessage {
    private Long userId;
    private String username;
    private String roomCode;
    private double lat; // 위도
    private double lng; // 경도
}
