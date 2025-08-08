package com.example.whereshouldwego.controller;

import com.example.whereshouldwego.dto.response.CreateRoomResponse;
import com.example.whereshouldwego.dto.response.RoomResponse;
import com.example.whereshouldwego.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;

    // 약속 방 생성
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateRoomResponse post(){

        return roomService.createRoom();
    }

    // 약속방 url 조회
    @GetMapping("/{roomCode}")
    public ResponseEntity<RoomResponse> get(@PathVariable("roomCode") String roomCode){
        RoomResponse roomResponse = roomService.getRoomByCode(roomCode);
        return ResponseEntity.ok(roomResponse);
    }
}
