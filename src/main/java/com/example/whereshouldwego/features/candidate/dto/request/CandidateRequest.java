package com.example.whereshouldwego.features.candidate.dto.request;

import com.example.whereshouldwego.features.candidate.domain.type.CandidateActionType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CandidateRequest {
    @NotNull @Positive
    private Long placeId;

    @NotNull
    private CandidateActionType actionType;
}
