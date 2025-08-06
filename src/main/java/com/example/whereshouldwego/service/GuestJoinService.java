package com.example.whereshouldwego.service;

import com.example.whereshouldwego.domain.Refresh;
import com.example.whereshouldwego.domain.User;
import com.example.whereshouldwego.dto.response.TokenResponse;
import com.example.whereshouldwego.jwt.JWTUtil;
import com.example.whereshouldwego.repository.postgres.RefreshRepository;
import com.example.whereshouldwego.repository.postgres.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class GuestJoinService {

    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    public GuestJoinService(UserRepository userRepository, JWTUtil jwtUtil, RefreshRepository refreshRepository) {

        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
    }

    public TokenResponse guestJoinProcess() {

        // UUID로 고유한 비회원용 username 생성
        String username = "guest " + UUID.randomUUID().toString();
        String role = "ROLE_GUEST";

        // User 엔티티에 저장
        User user = new User();

        user.setUsername(username);
        user.setRole(role);

        userRepository.save(user);

        // 새로운 Access Token 및 Refresh Token 생성
        String access = jwtUtil.createJwt("access", username, role, 3600000L);  // 1시간
        String refresh = jwtUtil.createJwt("refresh", username, role, 1209600000L); // 14일

        // Refresh Token 저장
        addRefreshEntity(username, refresh, 1209600000L);

        // 응답 설정
        return new TokenResponse(access, refresh);
    }

    private void addRefreshEntity(String username, String refresh, Long expiredMs) {

        Date date = new Date(System.currentTimeMillis() + expiredMs);

        Refresh refreshEntity = new Refresh();
        refreshEntity.setUsername(username);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(date.toString());

        refreshRepository.save(refreshEntity);
    }
}
