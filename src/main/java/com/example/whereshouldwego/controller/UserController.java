package com.example.whereshouldwego.controller;

import com.example.whereshouldwego.dto.request.AuthUpgradeRequest;
import com.example.whereshouldwego.dto.request.NicknameRequest;
import com.example.whereshouldwego.dto.response.CustomUserDetails;
import com.example.whereshouldwego.dto.response.TokenResponse;
import com.example.whereshouldwego.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class UserController {

    private final UserService userService;

    @PatchMapping("/nickname")
    public ResponseEntity<Void> changeNickname(@RequestBody NicknameRequest request, @AuthenticationPrincipal CustomUserDetails userDetails) {

        userService.changeNicknameProcess(request, userDetails);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/upgrade")
    public ResponseEntity<Void> upgradeGuestToUser(@RequestBody AuthUpgradeRequest request) {

        userService.authUpgradeProcess(request);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/guest")
    public ResponseEntity<Void> loginProcess(
            @CookieValue(value = "guest-refresh", required = false) String refresh,
            HttpServletResponse response) {

        // access 토큰, refresh 토큰 받아옴
        TokenResponse tokens = userService.guestLoginProcess(refresh);

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
