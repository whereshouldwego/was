package com.example.whereshouldwego.dto.response;

import com.example.whereshouldwego.domain.Place;
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

    public static CandidateResponse fromEntity(String roomCode, PlaceResponse place, int voteCount) {
        return CandidateResponse.builder()
                .roomCode(roomCode)
                .place(place)
                .voteCount(voteCount)
                .build();
    }
}
