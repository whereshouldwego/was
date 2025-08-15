package com.example.whereshouldwego.controller;

import com.example.whereshouldwego.dto.request.CreateFavoriteRequest;
import com.example.whereshouldwego.dto.response.CreateFavoriteResponse;
import com.example.whereshouldwego.dto.response.CustomUserDetails;
import com.example.whereshouldwego.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
@PreAuthorize("hasRole('MEMBER')")
public class FavoriteController {
    private final FavoriteService favoriteService;

    @PostMapping
    public ResponseEntity<CreateFavoriteResponse> createFavorite(@RequestBody CreateFavoriteRequest request, @AuthenticationPrincipal CustomUserDetails userDetails){
        CreateFavoriteResponse response = favoriteService.createFavorite(request, userDetails);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CreateFavoriteResponse>> findByUserId(@AuthenticationPrincipal CustomUserDetails userDetails){
        return ResponseEntity.ok(favoriteService.findByUserId(userDetails));
    }

    @DeleteMapping("/{favoriteId}")
    public ResponseEntity<Void> deleteFavorite(@PathVariable("favoriteId") Long favoriteId, @AuthenticationPrincipal CustomUserDetails userDetails){
        favoriteService.delete(favoriteId, userDetails);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

}
