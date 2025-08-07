//package com.example.whereshouldwego.controller;
//
//import com.example.whereshouldwego.service.CandidateService;
//import org.springframework.messaging.handler.annotation.DestinationVariable;
//import org.springframework.messaging.handler.annotation.MessageMapping;
//
//public class CandidateController {
//
//    private final CandidateService candidateService;
//
//    @MessageMapping("/Candidate.{roomCode}")
//    public void handleCandidateMessage(CandidateMessage message, @DestinationVariable String roomCode) {
//        candidateService.handleIncomingCandidate(message, roomCode);
//    }
//}
