package com.example.whereshouldwego.dto.request;

import com.example.whereshouldwego.domain.type.CandidateActionType;
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
