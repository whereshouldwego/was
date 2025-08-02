package com.example.whereshouldwego.controller;

import com.example.whereshouldwego.domain.Room;
import com.example.whereshouldwego.dto.UpdateStartLocationRequest;
import com.example.whereshouldwego.dto.UpdateStartLocationResponse;
import com.example.whereshouldwego.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UpdateStartLocationController {
    private final RoomService roomService;
    @PatchMapping("/start-point/{roomCode}/{userId}")
    public UpdateStartLocationResponse updateStartPoint(@PathVariable("roomCode") String roomCode,
                                                        @PathVariable("userId") Long userId,
                                                        @RequestBody UpdateStartLocationRequest request){
        return roomService.updateLocation(request, roomCode, userId);
    }
}
