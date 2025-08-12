package com.example.whereshouldwego.controller;

import com.example.whereshouldwego.dto.request.AuthUpgradeRequest;
import com.example.whereshouldwego.dto.response.TokenResponse;
import com.example.whereshouldwego.service.GuestLoginService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GuestLoginController {

    private final GuestLoginService guestLoginService;

    @PostMapping("/api/auth/upgrade")
    public ResponseEntity<Void> upgradeGuestToUserProcess(@RequestBody AuthUpgradeRequest request) {

        Long guestId = request.getGuestId();
        Long memberId = request.getMemberId();

        guestLoginService.authUpgradeProcess(guestId, memberId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/api/auth/guest")
    public ResponseEntity<Void> loginProcess(
            @CookieValue(value = "guest-refresh", required = false) String refresh,
            HttpServletResponse response) {

        // access 토큰, refresh 토큰 받아옴
        TokenResponse tokens = guestLoginService.guestLoginProcess(refresh);

        // access 토큰을 Authorization 헤더에 담아 반환
        response.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + tokens.getAccessToken());

        // refresh 토큰을 쿠키에 담아 반환
        response.addCookie(createCookie("guest-refresh", tokens.getRefreshToken()));

        return ResponseEntity.ok().build();
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
