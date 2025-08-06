package com.example.whereshouldwego.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CreateFavoriteRequest {
    private Long userId;
    private Long placeId;
}
