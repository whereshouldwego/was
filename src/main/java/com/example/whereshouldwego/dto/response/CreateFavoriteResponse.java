package com.example.whereshouldwego.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class CreateFavoriteResponse {
    private Long favoriteId;
    private Long userId;
    private Long placeId;
}
