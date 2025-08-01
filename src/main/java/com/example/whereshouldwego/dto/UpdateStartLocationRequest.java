package com.example.whereshouldwego.dto;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateStartLocationRequest {
    private String startLocation;
}
