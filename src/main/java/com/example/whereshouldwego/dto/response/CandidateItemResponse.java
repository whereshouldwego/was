package com.example.whereshouldwego.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CandidateItemResponse {
    private PlaceResponse place;
    private int voteCount;

    public static CandidateItemResponse of(PlaceResponse place, int voteCount) {
        return CandidateItemResponse.builder()
                .place(place)
                .voteCount(voteCount)
                .build();
    }
}