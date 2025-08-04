package com.example.whereshouldwego.dto.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateStartLocationResponse {
    private Long userId;
    private String roomCode;
    private String startLocation;
}
