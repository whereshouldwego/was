package com.example.whereshouldwego.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PlaceRequest {
    private Long placeId;
}
