package com.example.whereshouldwego.controller;

import com.example.whereshouldwego.dto.request.CandidateRequest;
import com.example.whereshouldwego.dto.response.CandidateResponse;
import com.example.whereshouldwego.dto.response.CustomUserDetails;
import com.example.whereshouldwego.service.CandidateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import static com.example.whereshouldwego.util.RoomCodeUtil.decode;

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
