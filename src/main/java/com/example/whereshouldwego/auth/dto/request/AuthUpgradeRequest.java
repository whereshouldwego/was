package com.example.whereshouldwego.auth.dto.request;

import lombok.Getter;

@Getter
public class AuthUpgradeRequest {

    private Long guestId;
    private Long memberId;
}
