package com.example.whereshouldwego.controller;

import com.example.whereshouldwego.dto.request.ChatMessageRequestDto;
import com.example.whereshouldwego.dto.response.ChatMessageResponseDto;
import com.example.whereshouldwego.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @MessageMapping("/chat.{roomCode}")
    public void handleChatMessage(ChatMessageRequestDto message, @DestinationVariable String roomCode) {
        chatService.handleIncomingChat(message, roomCode);
    }

    @GetMapping("/api/chat/history/{roomCode}")
    public List<ChatMessageResponseDto> getChatHistory(@PathVariable String roomCode) {
        return chatService.getChatHistory(roomCode);
    }
}
