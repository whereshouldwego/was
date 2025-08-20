package com.example.whereshouldwego.features.room.dto.request;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateStartLocationRequest {
    private String startLocation;
}
