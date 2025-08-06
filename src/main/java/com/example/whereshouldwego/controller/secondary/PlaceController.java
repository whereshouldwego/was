package com.example.whereshouldwego.controller.secondary;

import com.example.whereshouldwego.dto.response.PlaceResponse;
import com.example.whereshouldwego.service.PlaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/places")
@RequiredArgsConstructor
public class PlaceController {
    private final PlaceService placeService;

    //특정 장소 조회
    @GetMapping("/{placeId}")
    public PlaceResponse get(@PathVariable("placeId") Long placeId){
        return placeService.getPlaceById(placeId);
    }
}
