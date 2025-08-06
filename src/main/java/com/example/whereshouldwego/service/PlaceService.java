package com.example.whereshouldwego.service;

import com.example.whereshouldwego.domain.Place;
import com.example.whereshouldwego.dto.response.PlaceResponse;
import com.example.whereshouldwego.repository.postgres.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaceService {
    private final PlaceRepository placeRepository;
    // 전체 장소  조회
    public List<PlaceResponse> getAllPlaces() {
        List<Place> places = placeRepository.findAll();
        return places.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }
    private PlaceResponse convertToResponse(Place place){
        return PlaceResponse.builder()
                .id(place.getId())
                .name(place.getName())
                .address(place.getAddress())
                .roadAddress(place.getRoadAddress())
                .phone(place.getPhone())
                .aiSummary(place.getAiSummary())
                .categoryName(place.getCategoryName())
                .build();
    }

    // 장소 id를 기준으로 특정 장소 조회
    public PlaceResponse getPlaceById(Long placeId){
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 장소가 존재하지 않습니다."));
        return convertToResponse(place);
    }
}
