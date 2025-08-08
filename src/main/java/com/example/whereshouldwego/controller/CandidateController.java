package com.example.whereshouldwego.controller;

import com.example.whereshouldwego.dto.request.CandidateMessageRequestDto;
import com.example.whereshouldwego.dto.response.CandidateMessageResponseDto;
import com.example.whereshouldwego.service.CandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@Controller
@RequiredArgsConstructor
public class CandidateController {

    private final CandidateService candidateService;

    @MessageMapping("/candidate.{roomCode}")
    public void handleCandidateMessage(CandidateMessageRequestDto message, @DestinationVariable String roomCode) {
        candidateService.handleIncomingCandidate(message, roomCode);
    }

    @GetMapping("api/candidate/history/{roomCode}")
    public ResponseEntity<List<CandidateMessageResponseDto>> getCandidateHistory(@PathVariable String roomCode) {
        return ResponseEntity.ok(candidateService.getCandidateHistory(roomCode));
    }
}
