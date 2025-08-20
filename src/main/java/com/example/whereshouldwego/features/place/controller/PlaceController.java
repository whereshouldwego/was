package com.example.whereshouldwego.features.place.controller;

import com.example.whereshouldwego.features.place.dto.request.EnsurePlacesRequest;
import com.example.whereshouldwego.features.place.dto.response.PlaceResponse;
import com.example.whereshouldwego.features.place.service.PlaceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/places")
@RequiredArgsConstructor
public class PlaceController {
    private final PlaceService placeService;

    //특정 장소 조회
    @GetMapping("/{placeId}")
    public ResponseEntity<PlaceResponse> get(@PathVariable("placeId") Long placeId){
        PlaceResponse place = placeService.getPlaceById(placeId);
        return ResponseEntity.ok(place);
    }

    @PostMapping("/ensure-batch")
    public ResponseEntity<List<PlaceResponse>> ensureBatch(@RequestBody @Valid EnsurePlacesRequest request) {
        List<PlaceResponse> created = placeService.saveIfAbsentAll(request.toEntities());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
