package com.example.whereshouldwego.dto.request;

import com.example.whereshouldwego.domain.type.CandidateActionType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CandidateMessageRequestDto {
    private Long userId;
    private String roomCode;
    private Long placeId;
    private CandidateActionType actionType;
}
