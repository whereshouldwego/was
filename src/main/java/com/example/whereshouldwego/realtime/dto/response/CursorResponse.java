package com.example.whereshouldwego.realtime.dto.response;

import com.example.whereshouldwego.realtime.dto.request.CursorRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CursorResponse {
    private String username;
    private Double lat;
    private Double lng;

    public static CursorResponse from(CursorRequest dto) {
        return CursorResponse.builder()
                .username(dto.getUsername())
                .lat(dto.getLat())
                .lng(dto.getLng())
                .build();
    }
}