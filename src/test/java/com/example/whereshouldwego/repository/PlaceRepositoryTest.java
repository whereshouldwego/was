package com.example.whereshouldwego.repository;


import com.example.whereshouldwego.domain.Place;
import com.example.whereshouldwego.repository.postgres.PlaceRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

@SpringBootTest
@ActiveProfiles("test")
public class PlaceRepositoryTest {
    @Autowired
    private PlaceRepository placeRepository;

    @Test
    @DisplayName("존재하는 placeId 기반으로 장소 조회하기")
    public void findExistingPlace(){
        Optional<Place> foundPlace = placeRepository.findById(29633L);
        Place place = foundPlace.get();
        assertThat(foundPlace).isPresent();
        System.out.println(place.getId());
        System.out.println(place.getName());
        System.out.println(place.getAddress());
        System.out.println(place.getAiSummary());
    }

    @Test
    @DisplayName("존재하지 않는 placeId 기반으로 장소 조회하기")
    public void findNonExistingPlace(){
        Optional<Place> place = placeRepository.findById(1L);
        assertThat(place).isEmpty();
    }
}
