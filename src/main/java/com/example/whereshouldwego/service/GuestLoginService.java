package com.example.whereshouldwego.service;

import com.example.whereshouldwego.domain.Refresh;
import com.example.whereshouldwego.domain.Room;
import com.example.whereshouldwego.domain.RoomParticipant;
import com.example.whereshouldwego.domain.User;
import com.example.whereshouldwego.dto.response.GuestLoginResponse;
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
    private final RoomRepository roomRepository;
    private final RoomParticipantRepository roomParticipantRepository;
    private final JWTUtil jwtUtil;
    private final Random random = new Random();

    private final List<String> nouns = Arrays.asList(
            "유니콘", "도깨비", "스핑크스", "드래곤", "구미호", "마법사", "닌자", "연금술사", "모험가", "기사"
    );

    public GuestLoginResponse guestLoginProcess(String refresh, String roomCode) {

        // 방 찾기
        Room room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new RuntimeException("약속방이 존재하지 않습니다."));

        User user;

        // refresh 토큰이 없으면 새로운 비회원 생성
        if (refresh == null) {

            User newUser = User.builder()
                    .username("guest " + UUID.randomUUID().toString())
                    .role("ROLE_GUEST")
                    .build();
            user = userRepository.save(newUser);
        }
        // refresh 토큰이 있으면 비회원 로그인
        else {

            // refresh 토큰 검증
            if (jwtUtil.isExpired(refresh)) {
                throw new RuntimeException("Refresh Token이 만료됐습니다.");
            }
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

        // 약속방 참여
        RoomParticipant participant = roomParticipantRepository.findByRoomAndUser(room, user)
                .orElseGet(() -> {

                    // 닉네임 생성
                    String nickname;
                    do {
                        String noun = nouns.get(random.nextInt(nouns.size()));
                        nickname = "익명의 " + noun;
                    } while (roomParticipantRepository.existsByRoomAndNickname(room, nickname));

                    // 약속방 참여 관계 생성 및 저장
                    RoomParticipant newParticipant = RoomParticipant.builder()
                            .user(user)
                            .room(room)
                            .nickname(nickname)
                            .build();
                    return roomParticipantRepository.save(newParticipant);
                });

        // 토큰 생성
        String newAccess = jwtUtil.createJwt("access", user.getUsername(), user.getRole(), 3600000L);
        String newRefresh = jwtUtil.createJwt("refresh", user.getUsername(), user.getRole(), 1209600000L);

        // 토큰 저장
        Refresh savedRefresh = Refresh.builder()
                .username(user.getUsername())
                .refresh(newRefresh)
                .expiration(LocalDateTime.now().plusSeconds(1209600000L / 1000))
                .build();
        refreshRepository.save(savedRefresh);

        // 최종 응답 DTO 생성
        return new GuestLoginResponse(user.getId(), participant.getNickname(), newAccess, newRefresh);
    }
}
