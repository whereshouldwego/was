package com.example.whereshouldwego.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class GuestLoginResponse {

    private Long userId;
    private String nickname;
    private String accessToken;
    private String refreshToken;
}
