package com.example.whereshouldwego.dto.response;

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
}
