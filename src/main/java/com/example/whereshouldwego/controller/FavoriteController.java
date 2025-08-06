package com.example.whereshouldwego.controller;

import com.example.whereshouldwego.dto.request.CreateFavoriteRequest;
import com.example.whereshouldwego.dto.response.CreateFavoriteResponse;
import com.example.whereshouldwego.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {
    private final FavoriteService favoriteService;

    @PostMapping
    public ResponseEntity<CreateFavoriteResponse> createFavorite(@RequestBody CreateFavoriteRequest request){
        CreateFavoriteResponse response = favoriteService.createFavorite(request);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<CreateFavoriteResponse>> findByUserId(@PathVariable("userId") Long userId){
        return ResponseEntity.ok(favoriteService.findByUserId(userId));
    }

    @DeleteMapping("/{favoriteId}")
    public ResponseEntity<Void> deleteFavorite(@PathVariable("favoriteId") Long favoriteId){
        favoriteService.delete(favoriteId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
