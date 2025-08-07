package com.example.whereshouldwego.service;


import com.example.whereshouldwego.domain.Room;
import com.example.whereshouldwego.domain.RoomParticipant;
import com.example.whereshouldwego.domain.User;
import com.example.whereshouldwego.dto.request.CreateRoomRequest;
import com.example.whereshouldwego.dto.request.JoinRoomRequest;
import com.example.whereshouldwego.dto.request.UpdateStartLocationRequest;
import com.example.whereshouldwego.dto.response.CreateRoomResponse;
import com.example.whereshouldwego.dto.response.JoinRoomResponse;
import com.example.whereshouldwego.dto.response.RoomResponse;
import com.example.whereshouldwego.dto.response.UpdateStartLocationResponse;
import com.example.whereshouldwego.repository.postgres.RoomParticipantRepository;
import com.example.whereshouldwego.repository.postgres.RoomRepository;
import com.example.whereshouldwego.repository.postgres.UserRepository;
import com.example.whereshouldwego.util.RoomCodeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoomService {
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final RoomParticipantRepository roomParticipantRepository;
    private final String BASE_URL = "https://localhost:8000/";

    public CreateRoomResponse createRoom(CreateRoomRequest request) {
        // 1. 처음 저장 -> 이유: room의 id 생성을 위해
        User user;

        // 회원 비회원 확인 로직
        if (request.getUserId() ==null){
            user = new User();
            user = userRepository.save(user);
        } else {
            user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("User Not Found"));
        }

        Room room = new Room();
        room.setUser(user);
        Room saved = roomRepository.save(room);

        String roomCode = RoomCodeUtil.encode(saved.getId());

        //3. URL 생성
        String roomUrl = BASE_URL + roomCode;

        //4. 엔티티에 다시 set 후 저장
        saved.setRoomCode(roomCode);
        saved.setRoomUrl(roomUrl);
        roomRepository.save(saved);

        return CreateRoomResponse.builder()
                .roomCode(saved.getRoomCode())
                .roomUrl(saved.getRoomUrl())
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
    public JoinRoomResponse joinRoom(JoinRoomRequest request, String roomCode){
        User user;

        // 방 참여자가 회원인지 비회원인지 확인하는 로직
        if (request.getUserId() ==null){
            user = new User();
            user = userRepository.save(user);
        } else {
            user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new IllegalArgumentException("User Not Found"));
        }
        // 방이 존재하는지 확인하는 로직
        Room room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new IllegalArgumentException("Room Not Found"));

        // 방 참여자를 생성하는 로직
        RoomParticipant roomParticipant = RoomParticipant.builder()
                .user(user)
                .room(room)
                .build();
        roomParticipantRepository.save(roomParticipant);

        return JoinRoomResponse.builder()
                .userId((long) user.getId())
                .roomCode(room.getRoomCode())
                .build();
    }
    public UpdateStartLocationResponse updateLocation(UpdateStartLocationRequest request, String roomCode, Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Room room = roomRepository.findByRoomCode(roomCode)
                .orElseThrow(() -> new IllegalArgumentException("Room not found"));

        RoomParticipant roomParticipant = roomParticipantRepository.findByRoomIdAndUserId(user,room)
                .orElseThrow(() -> new IllegalArgumentException("Participant not found"));

        roomParticipant.setStartLocation(request.getStartLocation());
        roomParticipantRepository.save(roomParticipant);

        return UpdateStartLocationResponse.builder()
                .userId((long) user.getId())
                .roomCode(room.getRoomCode())
                .startLocation(request.getStartLocation()).build();
    }
}
