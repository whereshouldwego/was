package com.example.whereshouldwego.features.room.controller;

import com.example.whereshouldwego.features.room.dto.request.UpdateStartLocationRequest;
import com.example.whereshouldwego.features.room.dto.response.UpdateStartLocationResponse;
import com.example.whereshouldwego.features.room.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UpdateStartLocationController {
    private final RoomService roomService;
    @PatchMapping("/start-point/{roomCode}/{userId}")
    public ResponseEntity<UpdateStartLocationResponse> updateStartPoint(@PathVariable("roomCode") String roomCode,
                                                                        @PathVariable("userId") Long userId,
                                                                        @RequestBody UpdateStartLocationRequest request){
        UpdateStartLocationResponse update = roomService.updateLocation(request, roomCode, userId);
        return ResponseEntity.ok(update);
    }
}
