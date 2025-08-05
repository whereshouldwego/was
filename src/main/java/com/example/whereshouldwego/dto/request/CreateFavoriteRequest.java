package com.example.whereshouldwego.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateFavoriteRequest {
    private Long userId;
    private Long placeId;
}
