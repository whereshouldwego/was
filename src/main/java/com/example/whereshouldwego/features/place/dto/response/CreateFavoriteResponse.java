package com.example.whereshouldwego.features.place.dto.response;

import com.example.whereshouldwego.features.place.domain.Favorite;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateFavoriteResponse {
    private Long favoriteId;
    private Long userId;
    private Long placeId;

    public static CreateFavoriteResponse from(Favorite favorite) {
        return CreateFavoriteResponse.builder()
                .favoriteId(favorite.getId())
                .userId(favorite.getUser().getId())
                .placeId(favorite.getPlace().getId())
                .build();
    }

    public static CreateFavoriteResponse of(Long favoriteId, Long userId, Long placeId) {
        return CreateFavoriteResponse.builder()
                .favoriteId(favoriteId)
                .userId(userId)
                .placeId(placeId)
                .build();
    }
}