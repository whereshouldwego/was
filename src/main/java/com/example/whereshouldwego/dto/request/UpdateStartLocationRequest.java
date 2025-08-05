package com.example.whereshouldwego.dto.request;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateStartLocationRequest {
    private String startLocation;
}
