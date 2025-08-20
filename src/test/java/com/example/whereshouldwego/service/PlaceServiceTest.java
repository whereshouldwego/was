package com.example.whereshouldwego.service;

import com.example.whereshouldwego.domain.Place;
import com.example.whereshouldwego.dto.response.PlaceResponse;
import com.example.whereshouldwego.repository.postgres.PlaceRepository;
import com.example.whereshouldwego.service.PlaceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
public class PlaceServiceTest {
    @MockitoBean
    private PlaceRepository placeRepository;

    @Autowired
    private PlaceService placeService;

    @Test
    @DisplayName("placeId로 장소 조회 - 성공")
    public void getPlaceById(){
        Long id = 29633L;
        Place place = Place.builder()
                .id(id)
                .name("아디카페")
                .address("강원특별자치도 강릉시 주문진읍 교항리 48-32")
                .roadAddress("강원특별자치도 강릉시 주문진읍 연주로 297-1")
                .phone(null)
                .categoryName("카페").build();
        when(placeRepository.findById(id)).thenReturn(Optional.of(place));

        PlaceResponse result = placeService.getPlaceById(id);

        assertThat(result.getName()).isEqualTo("아디카페");
        assertThat(result.getId()).isEqualTo(id);
    }

    @Test
    @DisplayName("placeId로 장소 조회 _ 실패")
    public void findPlayIdWithWrongId(){
        Long id = 1L;
        when(placeRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            placeService.getPlaceById(id);
        });
        verify(placeRepository).findById(id);
    }
}
