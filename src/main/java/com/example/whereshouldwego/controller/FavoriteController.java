package com.example.whereshouldwego.controller;

import com.example.whereshouldwego.dto.request.CreateFavoriteRequest;
import com.example.whereshouldwego.dto.response.CreateFavoriteResponse;
import com.example.whereshouldwego.dto.response.CustomUserDetails;
import com.example.whereshouldwego.service.FavoriteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('MEMBER')")
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping
    public ResponseEntity<CreateFavoriteResponse> createFavorite(@RequestBody @Valid CreateFavoriteRequest request,
                                                                 @AuthenticationPrincipal CustomUserDetails user
    ) {
        CreateFavoriteResponse response = favoriteService.createFavorite(request, user.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<CreateFavoriteResponse>> findMyFavorites(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(favoriteService.findMyFavorites(user.getUsername()));
    }

    @DeleteMapping("/{favoriteId}")
    public ResponseEntity<Void> deleteFavorite(@PathVariable Long favoriteId,
                                               @AuthenticationPrincipal CustomUserDetails user
    ) {
        favoriteService.deleteFavorite(favoriteId, user.getUsername());
        return ResponseEntity.noContent().build();
    }
}
