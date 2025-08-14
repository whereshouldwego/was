package com.example.whereshouldwego.controller;

import com.example.whereshouldwego.dto.request.ChatRequest;
import com.example.whereshouldwego.dto.response.ChatResponse;
import com.example.whereshouldwego.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
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

    @MessageMapping("/chat.{roomCode}")
    public void handleChatMessage(@Valid ChatRequest message,
                                  @DestinationVariable String roomCode
    ) {
        chatService.handleIncomingChat(message, roomCode);
    }

    @GetMapping("/{roomCode}/history")
    public ResponseEntity<List<ChatResponse>> getChatHistory(@PathVariable String roomCode) {
        return ResponseEntity.ok(chatService.getChatHistory(roomCode));
    }
}
