package com.example.whereshouldwego.controller;

import com.example.whereshouldwego.dto.response.GuestLoginResponse;
import com.example.whereshouldwego.dto.response.LoginSuccessResponse;
import com.example.whereshouldwego.service.GuestLoginService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GuestLoginController {

    private final GuestLoginService guestLoginService;

    @PostMapping("/api/auth/guest")
    public ResponseEntity<LoginSuccessResponse> loginProcess(
            @CookieValue(value = "refresh", required = false) String refresh,
            @RequestParam String roomCode,
            HttpServletResponse response) {

        // access 토큰, refresh 토큰, nickname, userId 받아옴
        GuestLoginResponse guestLoginResponse = guestLoginService.guestLoginProcess(refresh, roomCode);

        // 새로운 refresh 토큰을 쿠키에 담아 반환
        response.addCookie(createCookie("refresh", guestLoginResponse.getRefreshToken()));

        // access 토큰, nickname, userId만 반환
        LoginSuccessResponse loginSuccessResponse = new LoginSuccessResponse(
                guestLoginResponse.getUserId(),
                guestLoginResponse.getNickname(),
                guestLoginResponse.getAccessToken()
        );

        return ResponseEntity.ok(loginSuccessResponse);
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(60 * 60 * 24 * 14);
        //cookie.setSecure(true);
        //cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }
}
