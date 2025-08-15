package com.example.whereshouldwego.controller;

import com.example.whereshouldwego.dto.request.CandidateRequest;
import com.example.whereshouldwego.dto.response.CandidateResponse;
import com.example.whereshouldwego.dto.response.CustomUserDetails;
import com.example.whereshouldwego.service.CandidateService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/candidates")
@RequiredArgsConstructor
public class CandidateController {

    private final CandidateService candidateService;

    @MessageMapping("/candidate.{roomCode}")
    public void handleCandidateMessage(@Valid @Payload CandidateRequest request,
                                       Authentication authentication,
                                       @DestinationVariable String roomCode
    ) {
        CandidateResponse response = candidateService.handleCandidates(request, authentication, roomCode);
        candidateService.broadcastCandidates(roomCode, response);
    }

    @GetMapping("/history/{roomCode}")
    public ResponseEntity<CandidateResponse> getCandidateHistory(@PathVariable String roomCode) {
        return ResponseEntity.ok(candidateService.getCandidateHistory(roomCode));
    }
}
