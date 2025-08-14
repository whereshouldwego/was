package com.example.whereshouldwego.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cursor {
    private String roomCode;
    private String username;
    private Double lat; // 위도
    private Double lng; // 경도
}
