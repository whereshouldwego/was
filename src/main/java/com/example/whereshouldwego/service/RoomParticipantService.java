package com.example.whereshouldwego.service;

import com.example.whereshouldwego.domain.Room;
import com.example.whereshouldwego.domain.RoomParticipant;
import com.example.whereshouldwego.domain.User;
import com.example.whereshouldwego.dto.response.CustomUserDetails;
import com.example.whereshouldwego.dto.response.RoomParticipantResponse;
import com.example.whereshouldwego.repository.postgres.RoomParticipantRepository;
import com.example.whereshouldwego.repository.postgres.RoomRepository;
import com.example.whereshouldwego.repository.postgres.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomParticipantService {

    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final RoomParticipantRepository roomParticipantRepository;
    private final Random random = new Random();

    private final List<String> nouns = Arrays.asList(
            "유니콘", "도깨비", "스핑크스", "드래곤", "구미호", "마법사", "닌자", "연금술사", "모험가", "기사"
    );

    public RoomParticipantResponse roomParticipateProcess(String roomCode, CustomUserDetails userDetails) {

        // 방(Room) 찾기
        Room room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new RuntimeException("해당 방 코드를 가진 방을 찾을 수 없습니다."));

        // 사용자(User) 찾기
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 기존 참여 기록 확인
        return roomParticipantRepository.findByRoomAndUser(room, user)
                .map(roomParticipant -> {

                    // 참여 기록이 있으면 해당 정보를 DTO로 변환하여 반환
                    return new RoomParticipantResponse(
                            user.getId(),
                            roomParticipant.getNickname()
                    );
                })
                .orElseGet(() -> {

                    // 참여 기록이 없으면 새로운 방 참여 관계 생성

                    // 방의 모든 참여 기록 가져오기
                    List<RoomParticipant> roomParticipants = roomParticipantRepository.findAllByRoom(room);
                    List<String> existNicknames = roomParticipants.stream()
                            .map(RoomParticipant::getNickname)
                            .collect(Collectors.toList());

                    if (roomParticipants.size() >= 10) {

                        throw new RuntimeException("방 참여자가 10명을 초과합니다.");
                    }

                    // 닉네임 및 색깔 생성
                    String nickname = null;

                    for (int i = 0; i < nouns.size(); i++) {
                        String candidateNickname = nouns.get(i);

                        // 닉네임과 색깔이 모두 사용 중이 아닐 때
                        if (!existNicknames.contains(candidateNickname)) {
                            nickname = "익명의 " + candidateNickname;
                            break;
                        }
                    }

                    RoomParticipant newParticipant = RoomParticipant.builder()
                            .room(room)
                            .user(user)
                            .nickname(nickname)
                            .build();

                    roomParticipantRepository.save(newParticipant);

                    return new RoomParticipantResponse(
                            user.getId(),
                            newParticipant.getNickname()
                    );
                });
    }
}
