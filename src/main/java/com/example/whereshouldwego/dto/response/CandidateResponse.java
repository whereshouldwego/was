package com.example.whereshouldwego.dto.response;

import com.example.whereshouldwego.domain.Candidate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CandidateResponse {
    private String roomCode;
    private PlaceResponse place;
    private int voteCount;

    public static CandidateResponse of(String roomCode, PlaceResponse place, int voteCount) {
        return CandidateResponse.builder()
                .roomCode(roomCode)
                .place(place)
                .voteCount(voteCount)
                .build();
    }

    public static CandidateResponse from(Candidate candidate, String roomCode, PlaceResponse place, int voteCount) {
        return of(roomCode, place, voteCount);
    }
}