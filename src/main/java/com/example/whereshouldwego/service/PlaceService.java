package com.example.whereshouldwego.service;

import com.example.whereshouldwego.domain.Place;
import com.example.whereshouldwego.dto.response.PlaceResponse;
import com.example.whereshouldwego.repository.postgres.PlaceRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaceService {
    private final PlaceRepository placeRepository;
    @PersistenceContext
    private EntityManager em;
    // 전체 장소  조회
    public List<PlaceResponse> getAllPlaces() {
        List<Place> places = placeRepository.findAll();
        return places.stream()
                .map(PlaceResponse::fromEntity)
                .collect(Collectors.toList());
    }

    // 장소 id를 기준으로 특정 장소 조회
    public PlaceResponse getPlaceById(Long placeId){
        Place place = placeRepository.findById(placeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 장소가 존재하지 않습니다."));
        return PlaceResponse.fromEntity(place);
    }

    @Transactional
    public List<PlaceResponse> saveIfAbsentAll(Collection<Place> candidates) {
        if (candidates == null || candidates.isEmpty()) {
            return List.of();
        }
        for (Place candidate : candidates) {
            placeRepository.insertOrIgnore(
                    candidate.getId(),
                    candidate.getName(),
                    candidate.getKakaoUrl(),
                    candidate.getX(),
                    candidate.getY(),
                    candidate.getAddress(),
                    candidate.getRoadAddress(),
                    candidate.getPhone(),
                    candidate.getCategoryCode(),
                    candidate.getCategoryName()
            );
        }
        Set<Long> insertedIds = candidates.stream().map(Place::getId).collect(Collectors.toSet());
        List<Place> insertedPlaces = placeRepository.findAllById(insertedIds);

        return insertedPlaces.stream()
                .map(PlaceResponse::fromEntity)
                .toList();
    }
}