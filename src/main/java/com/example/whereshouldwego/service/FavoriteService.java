package com.example.whereshouldwego.service;

import com.example.whereshouldwego.domain.Favorite;
import com.example.whereshouldwego.domain.User;
import com.example.whereshouldwego.dto.request.CreateFavoriteRequest;
import com.example.whereshouldwego.dto.response.CreateFavoriteResponse;
import com.example.whereshouldwego.repository.FavoriteRepository;
import com.example.whereshouldwego.repository.UserRepository;
import com.example.whereshouldwego.repository.secondary.PlaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FavoriteService {
    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;
    private final FavoriteRepository favoriteRepository;

    private CreateFavoriteResponse createFavorite(CreateFavoriteRequest request){
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다. "));
        Favorite favorite = Favorite.builder()
                .user(user)
                .placeId(request.getPlaceId())
                .build();
        Favorite saved = favoriteRepository.save(favorite);
        return CreateFavoriteResponse.builder()
                .favoriteId(saved.getId())
                .userId(saved.getUser().getId())
                .placeId(saved.getPlaceId())
                .build();
    }

}
