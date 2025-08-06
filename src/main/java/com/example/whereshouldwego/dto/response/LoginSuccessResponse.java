package com.example.whereshouldwego.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginSuccessResponse {

    private Long userId;
    private String nickname;
    private String accessToken;
}
