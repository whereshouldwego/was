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

    /**
     * 비회원 로그인/생성 로직의 전체 흐름을 처리하는 메인 메서드
     */
    public GuestLoginResponse guestLoginProcess(String refresh, String roomCode) {
        Room room = findRoom(roomCode);
        User user;

        if (refresh == null) {
            // refresh 토큰이 없으면 새로운 비회원 생성
            user = createNewGuestUser();
        } else {
            // refresh 토큰이 있으면 기존 비회원 로그인 처리
            user = validateAndGetUser(refresh);
            refreshRepository.deleteByRefresh(refresh);
        }

        // 약속방 참여 처리
        RoomParticipant participant = createOrFindParticipant(user, room);

        // 토큰 발급 및 저장
        return issueTokensAndCreateResponse(user, participant.getNickname());
    }

    // --- 재사용성을 높인 핵심 로직 분리 ---

    /**
     * 새로운 비회원 사용자를 생성합니다.
     */
    private User createNewGuestUser() {
        User user = new User();
        user.setUsername("guest " + UUID.randomUUID().toString());
        user.setRole("ROLE_GUEST");
        return userRepository.save(user);
    }

    /**
     * Refresh 토큰을 검증하고, 토큰에 담긴 사용자 정보를 반환합니다.
     */
    private User validateAndGetUser(String refresh) {
        validateRefreshToken(refresh);
        String guestUsername = jwtUtil.getUsername(refresh);
        return userRepository.findByUsername(guestUsername)
                .orElseThrow(() -> new RuntimeException("해당 비회원 정보가 존재하지 않습니다."));
    }

    /**
     * 사용자가 약속방에 이미 참여했는지 확인하고, 없으면 새로 생성합니다.
     */
    private RoomParticipant createOrFindParticipant(User user, Room room) {
        return roomParticipantRepository.existsByRoomAndUsername(room, user.getUsername())
                .orElseGet(() -> {
                    RoomParticipant newParticipant = new RoomParticipant();
                    newParticipant.setUser(user);
                    newParticipant.setRoom(room);
                    newParticipant.setParticipatedAt(LocalDateTime.now());
                    newParticipant.setNickname(generateUniqueNickname(room));
                    return roomParticipantRepository.save(newParticipant);
                });
    }

    /**
     * Access/Refresh 토큰을 발급하고 DB에 저장 후 응답 DTO를 생성합니다.
     */
    private GuestLoginResponse issueTokensAndCreateResponse(User user, String nickname) {
        String newAccess = jwtUtil.createJwt("access", user.getUsername(), user.getRole(), 3600000L);
        String newRefresh = jwtUtil.createJwt("refresh", user.getUsername(), user.getRole(), 1209600000L);

        addRefreshEntity(user.getUsername(), newRefresh, 1209600000L);

        // 최종 반환 DTO로 가공
        return new GuestLoginResponse(user.getId(), nickname, newAccess, newRefresh);
    }

    // --- 기타 헬퍼 메서드 ---

    private Room findRoom(String roomCode) {
        return roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new RuntimeException("약속방이 존재하지 않습니다."));
    }

    private void validateRefreshToken(String refresh) {
        if (jwtUtil.isExpired(refresh)) {
            throw new RuntimeException("Refresh Token이 만료됐습니다.");
        }
        if (!jwtUtil.getCategory(refresh).equals("refresh")) {
            throw new RuntimeException("Refresh Token이 유효하지 않습니다.");
        }
        if (!refreshRepository.existsByRefresh(refresh)) {
            throw new RuntimeException("Refresh Token이 DB에 존재하지 않습니다.");
        }
    }

    private String generateUniqueNickname(Room room) {
        String nickname;
        do {
            String noun = nouns.get(random.nextInt(nouns.size()));
            nickname = "익명의 " + noun;
        } while (roomParticipantRepository.existsByRoomAndNickname(room, nickname));
        return nickname;
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
