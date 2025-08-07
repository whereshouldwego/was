package com.example.whereshouldwego.service;


import com.example.whereshouldwego.domain.Room;
import com.example.whereshouldwego.domain.RoomParticipant;
import com.example.whereshouldwego.domain.User;
import com.example.whereshouldwego.dto.request.UpdateStartLocationRequest;
import com.example.whereshouldwego.dto.response.CreateRoomResponse;
import com.example.whereshouldwego.dto.response.RoomResponse;
import com.example.whereshouldwego.dto.response.UpdateStartLocationResponse;
import com.example.whereshouldwego.repository.postgres.RoomParticipantRepository;
import com.example.whereshouldwego.repository.postgres.RoomRepository;
import com.example.whereshouldwego.repository.postgres.UserRepository;
import com.example.whereshouldwego.util.RoomCodeUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final RoomParticipantRepository roomParticipantRepository;
    private final String BASE_URL = "https://localhost:8000/";

    public CreateRoomResponse createRoom() {

        // 1. 초기 Room 엔티티 생성 및 저장
        Room room = Room.builder().build();
        Room saved = roomRepository.save(room);

        // 2. 저장된 엔티티의 ID를 사용하여 고유한 코드 생성
        String roomCode = RoomCodeUtil.encode(saved.getId());

        // 3. URL 생성
        String roomUrl = BASE_URL + roomCode;

        // 4. 기존 엔티티를 toBuilder()로 복사하고, roomCode와 roomUrl 필드를 업데이트
        Room updatedRoom = saved.toBuilder()
                .roomCode(roomCode)
                .roomUrl(roomUrl)
                .build();

        // 5. 업데이트된 엔티티를 저장 (JPA가 변경을 감지하여 업데이트)
        roomRepository.save(updatedRoom);

        return CreateRoomResponse.builder()
                .roomCode(updatedRoom.getRoomCode())
                .roomUrl(updatedRoom.getRoomUrl())
                .build();
    }

    public RoomResponse getRoomByCode(String roomCode){
        Room room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        return RoomResponse.builder()
                .roomCode(room.getRoomCode())
                .roomUrl(room.getRoomUrl())
                .createdAt(room.getCreatedAt())
                .expiredAt(room.getExpiredAt())
                .build();
    }

    @Transactional
    public UpdateStartLocationResponse updateLocation(UpdateStartLocationRequest request, String roomCode, Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Room room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        RoomParticipant roomParticipant = roomParticipantRepository.findByRoomAndUser(room, user)
                .orElseThrow(() -> new IllegalArgumentException("Participant not found"));

        // toBuilder()를 사용하여 startLocation만 변경된 새로운 객체 생성
        RoomParticipant updatedParticipant = roomParticipant.toBuilder()
                .startLocation(request.getStartLocation())
                .build();

        roomParticipantRepository.save(updatedParticipant);

        return UpdateStartLocationResponse.builder()
                .userId(updatedParticipant.getUser().getId()) // 업데이트된 객체에서 정보 가져옴
                .roomCode(updatedParticipant.getRoom().getRoomCode())
                .startLocation(updatedParticipant.getStartLocation()).build();
    }
}
