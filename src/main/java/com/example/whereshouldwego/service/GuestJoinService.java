package com.example.whereshouldwego.service;

import com.example.whereshouldwego.domain.User;
import com.example.whereshouldwego.dto.response.TokenResponseDto;
import com.example.whereshouldwego.jwt.JWTUtil;
import com.example.whereshouldwego.repository.postgres.UserRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GuestJoinService {

    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;

    public GuestJoinService(UserRepository userRepository, JWTUtil jwtUtil) {

        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    public TokenResponseDto guestJoinProcess() {

        // UUID로 고유한 비회원용 username 생성
        String username = UUID.randomUUID().toString();
        String role = "ROLE_GUEST";

        // User 엔티티에 저장
        User user = new User();

        user.setUsername(username);
        user.setRole(role);

        userRepository.save(user);

        // 새로운 Access Token 및 Refresh Token 생성
        String access = jwtUtil.createJwt("access", username, role, 3600000L);  // 1시간
        String refresh = jwtUtil.createJwt("refresh", username, role, 1209600000L); // 14일

        // 응답 설정
        return new TokenResponseDto(access, refresh);
    }
}
