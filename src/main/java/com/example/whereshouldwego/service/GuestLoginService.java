package com.example.whereshouldwego.service;

import com.example.whereshouldwego.domain.Refresh;
import com.example.whereshouldwego.domain.User;
import com.example.whereshouldwego.dto.response.TokenResponse;
import com.example.whereshouldwego.jwt.JWTUtil;
import com.example.whereshouldwego.repository.postgres.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class GuestLoginService {

    private final UserRepository userRepository;
    private final RefreshRepository refreshRepository;
    private final RoomParticipantRepository roomParticipantRepository;
    private final VoteRepository voteRepository;
    private final JWTUtil jwtUtil;

    @Transactional
    public void authUpgradeProcess(Long guestId, Long memberId) {

        User guest = userRepository.findById(guestId)
                .orElseThrow(() -> new IllegalArgumentException("비회원 정보를 찾을 수 없습니다."));

        User member = userRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        // 비회원의 여러 방 참여 기록을 회원의 기록으로 변경
        roomParticipantRepository.updateMemberIdByGuestId(guestId, memberId);

        // 비회원의 여러 투표 기록을 회원의 기록으로 변경
        voteRepository.updateMemberIdByGuestId(guestId, memberId);

        userRepository.delete(guest);
    }

    public TokenResponse guestLoginProcess(String refresh) {

        User user;

        // refresh 토큰이 없거나 만료된 경우 새로운 비회원 생성
        if (refresh == null || jwtUtil.isExpired(refresh)) {

            User newUser = User.builder()
                    .username("guest " + UUID.randomUUID().toString())
                    .role("ROLE_GUEST")
                    .build();
            user = userRepository.save(newUser);
        }
        // refresh 토큰이 있으면 비회원 로그인
        else {

            // refresh 토큰 검증
            if (!jwtUtil.getCategory(refresh).equals("refresh")) {
                throw new RuntimeException("Refresh Token이 유효하지 않습니다.");
            }
            if (!refreshRepository.existsByRefresh(refresh)) {
                throw new RuntimeException("Refresh Token이 DB에 존재하지 않습니다.");
            }

            // 사용자 정보 가져오기
            String username = jwtUtil.getUsername(refresh);
            user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("해당 비회원 정보가 존재하지 않습니다."));

            // 기존 Refresh 토큰 삭제
            refreshRepository.deleteByRefresh(refresh);
        }

        // access, refresh 토큰 생성
        String newAccess = jwtUtil.createJwt("access", user.getUsername(), user.getRole(), 3600000L);
        String newRefresh = jwtUtil.createJwt("refresh", user.getUsername(), user.getRole(), 1209600000L);

        // refresh 토큰 저장
        Refresh savedRefresh = Refresh.builder()
                .username(user.getUsername())
                .refresh(newRefresh)
                .expiration(LocalDateTime.now().plusSeconds(1209600000L / 1000))
                .build();
        refreshRepository.save(savedRefresh);

        // 최종 응답 DTO 생성
        return new TokenResponse(newAccess, newRefresh);
    }
}
