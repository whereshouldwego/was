package com.example.whereshouldwego.controller;

import com.example.whereshouldwego.dto.*;
import com.example.whereshouldwego.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rooms")
@RequiredArgsConstructor
public class RoomController {
    private final RoomService roomService;
    // 약속 방 생성
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CreateRoomResponse post(@RequestBody CreateRoomRequest request){
        return roomService.createRoom(request);
    }
    //약속방 참여 (request body: userId, response : userId, roomCode, url
    @PostMapping("/{roomCode}")
    public JoinRoomResponse post(@PathVariable("roomCode") String roomCode,@RequestBody JoinRoomRequest request){
        return roomService.joinRoom(request, roomCode);
    }

    // 약속방 url 조회
    @GetMapping("/{roomCode}")
    public RoomResponse get(@PathVariable("roomCode") String roomCode){
        return roomService.getRoomByCode(roomCode);
    }

    // 출발지 지정


}
