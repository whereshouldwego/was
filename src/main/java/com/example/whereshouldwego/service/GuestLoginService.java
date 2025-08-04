package com.example.whereshouldwego.service;

import com.example.whereshouldwego.domain.User;
import com.example.whereshouldwego.dto.response.TokenResponseDto;
import com.example.whereshouldwego.jwt.JWTUtil;
import com.example.whereshouldwego.repository.postgres.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GuestLoginService {

    private final JWTUtil jwtUtil;
    private final UserRepository userRepository;

    public GuestLoginService(JWTUtil jwtUtil, UserRepository userRepository) {

        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
    }

    public TokenResponseDto guestLoginProcess(String refreshToken) {

        // Refresh Token 만료 여부 확인
        if (jwtUtil.isExpired(refreshToken)) {

            throw new RuntimeException("Refresh Token이 만료됐습니다.");
        }

        // 토큰이 refresh인지 확인 (발급 시 페이로드에 명시)
        String category = jwtUtil.getCategory(refreshToken);

        if (!category.equals("refresh")) {

            throw new RuntimeException("Refresh Token이 유효하지 않습니다.");
        }

        // Refresh Token에서 비회원 ID 추출
        String guestUsername = jwtUtil.getUsername(refreshToken);

        // DB에서 해당 비회원 정보가 존재하는지 확인
        Optional<User> optionalUser = userRepository.findByUsername(guestUsername);

        if (optionalUser.isEmpty()) {

            throw new RuntimeException("해당 비회원 정보가 존재하지 않습니다.");
        }

        User guestUser = optionalUser.get();
        String username = guestUser.getUsername();
        String role = guestUser.getRole();

        // 새로운 Access Token 및 Refresh Token 생성
        String access = jwtUtil.createJwt("access", username, role, 3600000L);  // 1시간
        String refresh = jwtUtil.createJwt("refresh", username, role, 1209600000L); // 14일

        // 응답 설정
        return new TokenResponseDto(access, refresh);
    }
}
