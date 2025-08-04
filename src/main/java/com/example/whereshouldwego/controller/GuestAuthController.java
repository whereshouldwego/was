package com.example.whereshouldwego.controller;

import com.example.whereshouldwego.dto.response.TokenResponseDto;
import com.example.whereshouldwego.service.GuestJoinService;
import com.example.whereshouldwego.service.GuestLoginService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/guest")
public class GuestAuthController {

    private final GuestJoinService guestJoinService;
    private final GuestLoginService guestLoginService;

    public GuestAuthController(GuestJoinService guestJoinService, GuestLoginService guestLoginService) {

        this.guestJoinService = guestJoinService;
        this.guestLoginService = guestLoginService;
    }

    @PostMapping("/join")
    public ResponseEntity<String> joinProcess(HttpServletResponse response) {

        // 서비스에서 토큰 정보를 담은 DTO 반환 받음
        TokenResponseDto tokens = guestJoinService.guestJoinProcess();

        // Refresh Token을 쿠키에 담아 클라이언트에 전송
        Cookie cookie = new Cookie("refresh", tokens.getRefreshToken());
        cookie.setMaxAge(24 * 60 * 60);
        cookie.setHttpOnly(true);
        //cookie.setSecure(true);         // HTTPS에서만 전송
        //cookie.setPath("/");            // 모든 경로에서 유효
        response.addCookie(cookie);

        // Access Token을 응답 헤더에 담아 클라이언트에 전송
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + tokens.getAccessToken());

        return new ResponseEntity<>(headers, HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginProcess(HttpServletRequest request, HttpServletResponse response) {

        // HTTP 요청에서 "refresh" 쿠키를 찾음
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {

            if (cookie.getName().equals("refresh")) {

                refresh = cookie.getValue();
            }
        }

        // refresh token이 존재하지 않으면 에러 반환
        if (refresh == null) {

            return new ResponseEntity<>("Refresh Token이 쿠키에 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        // 서비스에서 토큰 정보를 담은 DTO 반환 받음
        TokenResponseDto tokens = guestLoginService.guestLoginProcess(refresh);

        // Refresh Token을 쿠키에 담아 클라이언트에 전송
        Cookie cookie = new Cookie("refresh", tokens.getRefreshToken());
        cookie.setMaxAge(24 * 60 * 60);
        cookie.setHttpOnly(true);
        //cookie.setSecure(true);         // HTTPS에서만 전송
        //cookie.setPath("/");            // 모든 경로에서 유효
        response.addCookie(cookie);

        // Access Token을 응답 헤더에 담아 클라이언트에 전송
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + tokens.getAccessToken());

        return new ResponseEntity<>(headers, HttpStatus.OK);
    }
}