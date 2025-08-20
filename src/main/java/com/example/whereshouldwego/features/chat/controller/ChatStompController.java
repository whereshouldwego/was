package com.example.whereshouldwego.features.chat.controller;

import com.example.whereshouldwego.features.chat.dto.request.ChatRequest;
import com.example.whereshouldwego.features.user.dto.response.CustomUserDetails;
import com.example.whereshouldwego.features.chat.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;


@Controller
@RequiredArgsConstructor
public class ChatStompController {

    private final ChatService chatService;

    @MessageMapping("/chat.{roomCode}")
    public void handleChatMessage(@Valid @Payload ChatRequest request,
                                  Authentication authentication,
                                  @DestinationVariable String roomCode
    ) {
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        Long userId = principal.getUserId();

        chatService.handleAndBroadcast(request, userId, roomCode);
    }
}