package com.example.whereshouldwego.features.place.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateFavoriteRequest {
    private Long placeId;
    private Long kakaoPlaceId;

    private String name;
    private Double x;
    private Double y;
    private String address;
    private String roadAddress;
    private String phone;
    private String categoryCode;
    private String categoryName;
    private String kakaoUrl;
}
