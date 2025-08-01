package com.example.whereshouldwego.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateRoomRequest {
    private Long userId;

}
