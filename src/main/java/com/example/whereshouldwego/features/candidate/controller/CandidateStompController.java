package com.example.whereshouldwego.features.candidate.controller;

import com.example.whereshouldwego.features.candidate.dto.request.CandidateRequest;
import com.example.whereshouldwego.features.candidate.dto.response.CandidateResponse;
import com.example.whereshouldwego.features.user.dto.response.CustomUserDetails;
import com.example.whereshouldwego.features.candidate.service.CandidateService;
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
        CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
        Long userId = principal.getUserId();

        CandidateResponse response = candidateService.handleCandidates(request, userId, roomCode);
        candidateService.broadcastCandidates(roomCode, response);
    }
}
