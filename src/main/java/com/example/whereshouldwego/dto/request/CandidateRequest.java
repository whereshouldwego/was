package com.example.whereshouldwego.dto.request;

import com.example.whereshouldwego.domain.Candidate;
import com.example.whereshouldwego.domain.type.CandidateActionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CandidateRequest {
    @Null private String roomCode;
    @NotNull private Long placeId;
    @NotNull private CandidateActionType actionType;

    public Candidate toEntity(Long roomId) {
        return Candidate.builder()
                .roomId(roomId)
                .placeId(this.placeId)
                .build();
    }
}
