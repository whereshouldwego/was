package com.example.whereshouldwego.mapper;

import com.example.whereshouldwego.domain.Candidate;
import com.example.whereshouldwego.domain.Place;
import com.example.whereshouldwego.dto.request.CandidateRequest;
import com.example.whereshouldwego.dto.response.CandidateResponse;
import com.example.whereshouldwego.dto.response.PlaceResponse;

public final class CandidateMapper {
    public static Candidate toEntity(CandidateRequest req, long roomId) {
        return Candidate.of(roomId, req.getPlaceId());
    }

    public static CandidateResponse toResponse(String roomCode, Place place, int voteCount) {
        return CandidateResponse.of(roomCode, PlaceResponse.from(place), voteCount);
    }
}
