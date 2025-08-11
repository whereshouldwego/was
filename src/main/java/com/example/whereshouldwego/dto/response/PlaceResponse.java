package com.example.whereshouldwego.dto.response;

import com.example.whereshouldwego.domain.Place;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PlaceResponse {
    private Long id;
    private String name;
    private String kakaoUrl;
    private Double lat;
    private Double lng;
    private String address;
    private String roadAddress;
    private String phone;
    private String aiSummary;
    private String categoryName;

    public static PlaceResponse fromEntity(Place p) {
        return PlaceResponse.builder()
                .id(p.getId())
                .name(p.getName())
                .kakaoUrl(p.getKakaoUrl())
                .lat(p.getX())
                .lng(p.getY())
                .address(p.getAddress())
                .roadAddress(p.getRoadAddress())
                .phone(p.getPhone())
                .aiSummary(p.getAiSummary() == null ? null : p.getAiSummary().toString())
                .categoryName(p.getCategoryName())
                .build();
    }
}
