package com.example.whereshouldwego.dto.response;

import com.example.whereshouldwego.domain.Place;
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

    public static CandidateMessageResponseDto fromEntity(String roomCode, Place place, List<Long> votedUserIds) {
        return CandidateMessageResponseDto.builder()
                .roomCode(roomCode)
                .place(PlaceResponse.from(place))
                .votedUserIds(votedUserIds)
                .voteCount(votedUserIds.size())
                .build();
    }
}
