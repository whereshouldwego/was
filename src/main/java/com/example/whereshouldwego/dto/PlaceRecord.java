package com.example.whereshouldwego.dto;

public record PlaceRecord(
        Long id,
        String name,
        String kakaoUrl,
        Double lat,
        Double lng,
        String address,
        String roadAddress,
        String phone,
        String aiSummary,
        String categoryName,
        int voteCount
) {}