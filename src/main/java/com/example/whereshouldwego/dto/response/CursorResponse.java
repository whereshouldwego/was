package com.example.whereshouldwego.dto.response;

import com.example.whereshouldwego.dto.request.CursorRequest;
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

    public static CursorResponse from(String username, CursorRequest dto) {
        return CursorResponse.builder()
                .username(username)
                .lat(dto.getLat())
                .lng(dto.getLng())
                .build();
    }
}