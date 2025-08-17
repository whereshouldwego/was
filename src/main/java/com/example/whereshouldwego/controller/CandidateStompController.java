package com.example.whereshouldwego.controller;

import com.example.whereshouldwego.dto.request.CandidateRequest;
import com.example.whereshouldwego.dto.response.CandidateResponse;
import com.example.whereshouldwego.service.CandidateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class CandidateStompController {

    private final CandidateService candidateService;

    @MessageMapping("/candidate.{roomCode}")
    public void handleCandidateMessage(@Valid @Payload CandidateRequest request,
                                       Authentication authentication,
                                       @DestinationVariable String roomCode
    ) {
        CandidateResponse response = candidateService.handleCandidates(request, authentication, roomCode);
        candidateService.broadcastCandidates(roomCode, response);
    }
}
