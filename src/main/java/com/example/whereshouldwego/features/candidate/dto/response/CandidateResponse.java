package com.example.whereshouldwego.features.candidate.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CandidateResponse {
    private String roomCode;
    private List<CandidateItemResponse> candidates;

    public static CandidateResponse of(String roomCode, List<CandidateItemResponse> items) {
        return CandidateResponse.builder()
                .roomCode(roomCode)
                .candidates(items)
                .build();
    }
}
