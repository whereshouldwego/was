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
    public void handleCandidateMessage(@Valid CandidateRequest request,
                                       @AuthenticationPrincipal CustomUserDetails userDetails,
                                       @DestinationVariable String roomCode
    ) {
        List<CandidateResponse> response = candidateService.handleAndBroadcast(request, userDetails, roomCode);
        candidateService.broadcastCandidates(roomCode, response);
    }

    @GetMapping("/history/{roomCode}")
    public ResponseEntity<List<CandidateResponse>> getCandidateHistory(@PathVariable String roomCode) {
        return ResponseEntity.ok(candidateService.getCandidateHistory(roomCode));
    }
}
