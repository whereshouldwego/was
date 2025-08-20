package com.example.whereshouldwego.features.chat.controller;

import com.example.whereshouldwego.features.chat.dto.response.ChatResponse;
import com.example.whereshouldwego.features.chat.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chats")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @GetMapping("/{roomCode}/history")
    public ResponseEntity<List<ChatResponse>> getChatHistory(@PathVariable String roomCode) {
        return ResponseEntity.ok(chatService.getChatHistory(roomCode));
    }
}
