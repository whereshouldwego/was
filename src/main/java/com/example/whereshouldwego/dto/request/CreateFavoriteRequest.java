package com.example.whereshouldwego.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class CreateFavoriteRequest {
    private Long userId;
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
