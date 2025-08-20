package com.example.whereshouldwego.auth.controller;

import com.example.whereshouldwego.auth.domain.RefreshToken;
import com.example.whereshouldwego.auth.security.jwt.JwtUtil;
import com.example.whereshouldwego.auth.repository.RefreshTokenRepository;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Date;

@RestController
@RequiredArgsConstructor
public class ReissueController {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @PostMapping("/api/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request, HttpServletResponse response) {

        // 쿠키에서 Refresh Token 추출
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("guest-refresh") || cookie.getName().equals("member-refresh")) {
                    refresh = cookie.getValue();
                    break;
                }
            }
        }

        // Refresh Token이 존재하지 않으면 에러 반환
        if (refresh == null) {

            return new ResponseEntity<>("Refresh Token이 쿠키에 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        // Refresh Token 만료 여부 확인
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {

            return new ResponseEntity<>("Refresh Token이 만료됐습니다.", HttpStatus.BAD_REQUEST);
        }

        // 토큰이 refresh인지 확인 (발급시 페이로드에 명시)
        String category = jwtUtil.getCategory(refresh);

        if (!category.equals("refresh")) {

            //response status code
            return new ResponseEntity<>("Refresh Token이 유효하지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        // DB에 저장되어 있는지 확인
        Boolean isExist = refreshTokenRepository.existsByRefresh(refresh);
        if (!isExist) {

            return new ResponseEntity<>("Refresh Token이 DB에 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
        }

        Long userId = jwtUtil.getUserId(refresh);
        String role = jwtUtil.getRole(refresh);

        // 새로운 Access Token 및 Refresh Token 생성
        String newAccess = jwtUtil.createJwt("access", userId, role, 3600000L);  // 1시간
        String newRefresh = jwtUtil.createJwt("refresh", userId, role, 1209600000L); // 14일

        // DB에 저장된 기존의 Refresh Token 삭제 후 새 Refresh Token 저장
        refreshTokenRepository.deleteByRefresh(refresh);
        addRefreshEntity(userId, newRefresh, 1209600000L);

        // Access Token은 헤더에 담고 Refresh Token은 쿠키에 담아 반환
        response.setHeader("access", newAccess);
        response.addCookie(createCookie("refresh", newRefresh));

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private void addRefreshEntity(Long userId, String refresh, Long expiredMs) {

        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshToken savedRefreshToken = RefreshToken.builder()
                .userId(userId)
                .refresh(refresh)
                .expiration(LocalDateTime.now().plusSeconds(1209600000L / 1000))
                .build();
        refreshTokenRepository.save(savedRefreshToken);
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