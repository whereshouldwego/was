package com.example.whereshouldwego.features.room.controller;

import com.example.whereshouldwego.features.room.dto.response.CreateRoomResponse;
import com.example.whereshouldwego.features.room.dto.response.RoomParticipantResponse;
import com.example.whereshouldwego.features.room.dto.response.RoomResponse;
import com.example.whereshouldwego.features.user.dto.response.CustomUserDetails;
import com.example.whereshouldwego.features.room.service.RoomParticipantService;
import com.example.whereshouldwego.features.room.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;
    private final RoomParticipantService roomParticipantService;

    // 약속 방 생성
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateRoomResponse post(){

        return roomService.createRoom();
    }

    // 약속 방 참여
    @PostMapping("/{roomCode}")
    public ResponseEntity<RoomParticipantResponse> roomParticipant(
            @PathVariable String roomCode,
            @AuthenticationPrincipal CustomUserDetails userDetails
            ) {

        RoomParticipantResponse response = roomParticipantService.roomParticipateProcess(roomCode, userDetails);

        return ResponseEntity.ok(response);
    }

    // 약속방 url 조회
    @GetMapping("/{roomCode}")
    public ResponseEntity<RoomResponse> get(@PathVariable("roomCode") String roomCode){
        RoomResponse roomResponse = roomService.getRoomByCode(roomCode);
        return ResponseEntity.ok(roomResponse);
    }
}
