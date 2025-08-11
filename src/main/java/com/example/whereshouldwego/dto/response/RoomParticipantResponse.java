package com.example.whereshouldwego.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RoomParticipantResponse {

    private Long userId;
    private String nickname;
}
