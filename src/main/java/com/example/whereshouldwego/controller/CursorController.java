package com.example.whereshouldwego.controller;

import com.example.whereshouldwego.domain.CursorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class CursorController {

    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/cursor")
    public void handleCursor(@Payload CursorMessage message) {
        messagingTemplate.convertAndSend(
                "/ws/subscribe/cursor/" + message.getRoomCode(),
                message
        );
    }
}
