package com.example.whereshouldwego.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CandidateMessageResponseDto {
    private String roomCode;
    private PlaceResponse place;
    private List<Long> votedUserIds;
    private int voteCount;
}
