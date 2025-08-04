package com.example.whereshouldwego.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class CursorMessage {
    private Long userId;
    private String roomCode;
    private double x;
    private double y;
}
