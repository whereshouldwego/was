package com.example.whereshouldwego.dto.response;

import com.example.whereshouldwego.domain.Place;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlaceResponse {
    private Long id;
    private String name;
    private String address;
    private String roadAddress;
    private String phone;
    private String aiSummary;
    private String categoryName;

    public static PlaceResponse from(Place p) {
        return PlaceResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .address(p.getAddress())
                .roadAddress(p.getRoadAddress())
                .phone(p.getPhone())
                .aiSummary(p.getAiSummary() == null ? null : p.getAiSummary().toString())
                .categoryName(p.getCategoryName())
                .build();
    }
}
