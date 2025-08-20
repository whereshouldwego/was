package com.example.whereshouldwego.features.place.dto.response;

import com.example.whereshouldwego.features.place.domain.Place;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Getter;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

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
    private String categoryName;
    private List<String> menu;
    private List<String> mood;
    private List<String> feature;
    private List<String> purpose;
    private String categoryDetail;

    // ObjectMapper 인스턴스를 하나만 생성하여 재사용
    private static final ObjectMapper objectMapper = new ObjectMapper();

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
                .categoryName(p.getCategoryName())
                // JSON 문자열을 List<String>으로 파싱
                .menu(parseJsonString(p.getMenu()))
                .mood(parseJsonString(p.getMood()))
                .feature(parseJsonString(p.getFeature()))
                .purpose(parseJsonString(p.getPurpose()))
                .categoryDetail(p.getCategoryDetail())
                .build();
    }

    // JSON 문자열을 List<String>으로 파싱하는 헬퍼 메서드
    private static List<String> parseJsonString(String jsonString) {
        if (jsonString == null) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(jsonString, new TypeReference<List<String>>() {});
        } catch (IOException e) {
            System.err.println("JSON 파싱 오류: " + e.getMessage());
            return Collections.emptyList();
        }
    }
}