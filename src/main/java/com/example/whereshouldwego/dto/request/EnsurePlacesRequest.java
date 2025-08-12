package com.example.whereshouldwego.dto.request;

import com.example.whereshouldwego.domain.Place;
import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
public class EnsurePlacesRequest {

    @NotEmpty
    @Valid
    private List<PlaceItem> items;

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PlaceItem {

        @NotNull
        @JsonAlias("id")
        private Long kakaoPlaceId;

        @NotBlank
        @JsonAlias({"place_name", "name"})
        private String name;

        @JsonAlias("place_url")
        private String kakaoUrl;

        @JsonAlias("x")
        private Double x; // lon

        @JsonAlias("y")
        private Double y; // lat

        @JsonAlias("address_name")
        private String address;

        @JsonAlias("road_address_name")
        private String roadAddress;

        private String phone;

        @JsonAlias("category_group_code")
        private String categoryCode;

        @JsonAlias({ "category_name", "category_group_name" })
        private String categoryName;

        public Place from() {
            return Place.builder()
                    .id(kakaoPlaceId)
                    .name(name)
                    .kakaoUrl(kakaoUrl)
                    .x(x)
                    .y(y)
                    .address(address)
                    .roadAddress(roadAddress)
                    .phone(phone)
                    .categoryCode(categoryCode)
                    .categoryName(categoryName)
                    .build();
        }
    }

    public List<Place> toEntities() {
        return items.stream()
                .map(PlaceItem::from)
                .toList();
    }
}
