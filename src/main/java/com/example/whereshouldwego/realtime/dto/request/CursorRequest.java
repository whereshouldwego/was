package com.example.whereshouldwego.realtime.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CursorRequest {
    private String username;
    private Double lat;
    private Double lng;
}