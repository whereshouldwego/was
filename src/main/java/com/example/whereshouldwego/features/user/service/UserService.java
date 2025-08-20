package com.example.whereshouldwego.features.user.service;

import com.example.whereshouldwego.auth.domain.RefreshToken;
import com.example.whereshouldwego.auth.repository.RefreshTokenRepository;
import com.example.whereshouldwego.features.room.domain.Room;
import com.example.whereshouldwego.features.room.domain.RoomParticipant;
import com.example.whereshouldwego.features.room.repository.RoomRepository;
import com.example.whereshouldwego.features.room.repository.RoomParticipantRepository;
import com.example.whereshouldwego.features.user.domain.User;
import com.example.whereshouldwego.auth.dto.request.AuthUpgradeRequest;
import com.example.whereshouldwego.features.room.dto.request.NicknameRequest;
import com.example.whereshouldwego.features.user.dto.response.CustomUserDetails;
import com.example.whereshouldwego.auth.dto.response.TokenResponse;
import com.example.whereshouldwego.auth.security.jwt.JwtUtil;
import com.example.whereshouldwego.features.user.repository.UserRepository;
import com.example.whereshouldwego.features.candidate.repository.VoteRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final RoomRepository roomRepository;
    private final RoomParticipantRepository roomParticipantRepository;
    private final VoteRepository voteRepository;
    private final JwtUtil jwtUtil;

    public void changeNicknameProcess(NicknameRequest request, CustomUserDetails userDetails) {

        String roomCode = request.getRoomCode();
        String newNickname = request.getNewNickname();
        String username = userDetails.getUsername();

        Room room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new IllegalArgumentException("방 정보를 찾을 수 없습니다."));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        RoomParticipant roomParticipant = roomParticipantRepository.findByRoomAndUser(room, user)
                .orElseThrow(() -> new IllegalArgumentException("해당 방 참여자를 찾을 수 없습니다."));

        RoomParticipant updatedParticipant = roomParticipant.toBuilder()
                .nickname(newNickname)
                .build();

        roomParticipantRepository.save(updatedParticipant);
    }

    @Transactional
    public void authUpgradeProcess(AuthUpgradeRequest request) {

        Long guestId = request.getGuestId();
        Long memberId = request.getMemberId();

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
            if (!refreshTokenRepository.existsByRefresh(refresh)) {
                throw new RuntimeException("Refresh Token이 DB에 존재하지 않습니다.");
            }

            // 사용자 정보 가져오기
            Long userId = jwtUtil.getUserId(refresh);
            user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("해당 비회원 정보가 존재하지 않습니다."));

            // 기존 Refresh 토큰 삭제
            refreshTokenRepository.deleteByRefresh(refresh);
        }

        // access, refresh 토큰 생성
        String newAccess = jwtUtil.createJwt("access", user.getId(), user.getRole(), 3600000L);
        String newRefresh = jwtUtil.createJwt("refresh", user.getId(), user.getRole(), 1209600000L);

        // refresh 토큰 저장
        RefreshToken savedRefreshToken = RefreshToken.builder()
                .userId(user.getId())
                .refresh(newRefresh)
                .expiration(LocalDateTime.now().plusSeconds(1209600000L / 1000))
                .build();
        refreshTokenRepository.save(savedRefreshToken);

        // 최종 응답 DTO 생성
        return new TokenResponse(newAccess, newRefresh);
    }
}
