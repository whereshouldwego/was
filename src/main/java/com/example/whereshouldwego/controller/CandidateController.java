package com.example.whereshouldwego.controller;

import com.example.whereshouldwego.dto.request.CandidateMessageRequestDto;
import com.example.whereshouldwego.service.CandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;


@Controller
@RequiredArgsConstructor
public class CandidateController {

    private final CandidateService candidateService;

    @MessageMapping("/candidate.{roomCode}")
    public void handleCandidateMessage(CandidateMessageRequestDto message,
                                       @DestinationVariable String roomCode) {
        candidateService.handleIncomingCandidate(message, roomCode);
    }
}
