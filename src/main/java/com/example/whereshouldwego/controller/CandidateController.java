package com.example.whereshouldwego.controller;

import com.example.whereshouldwego.dto.response.CandidateResponse;
import com.example.whereshouldwego.service.CandidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/candidates")
@RequiredArgsConstructor
public class CandidateController {

    private final CandidateService candidateService;

    @GetMapping("/history/{roomCode}")
    public ResponseEntity<CandidateResponse> getCandidateHistory(@PathVariable String roomCode) {
        return ResponseEntity.ok(candidateService.getCandidateHistory(roomCode));
    }
}
