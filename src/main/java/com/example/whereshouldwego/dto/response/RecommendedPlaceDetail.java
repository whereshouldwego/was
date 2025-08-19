package com.example.whereshouldwego.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendedPlaceDetail {
    private Long id; // 장소 고유 ID
    private String name; // 장소 이름
    private String category; // 장소 카테고리
    private String address; // 지번 주소
    private String roadAddress; // 도로명 주소
    private List<String> menu; // 메뉴 목록
    private List<String> mood; // 분위기 목록
    private List<String> feature; // 특징 목록
    private List<String> purpose; // 목적 목록
    private Double x; // 경도 (longitude)
    private Double y; // 위도 (latitude)
    private Double similarityScore; // 유사도 점수
    private Double finalScore;
    private Integer matchCount;
    private Double distanceKm; // (선택적) 사용자 위치로부터의 거리 (km)
}

