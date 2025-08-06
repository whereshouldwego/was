package com.example.whereshouldwego.service;

import com.example.whereshouldwego.domain.Favorite;
import com.example.whereshouldwego.domain.User;
import com.example.whereshouldwego.domain.secondary.Place;
import com.example.whereshouldwego.dto.request.CreateFavoriteRequest;
import com.example.whereshouldwego.dto.response.CreateFavoriteResponse;
import com.example.whereshouldwego.repository.postgres.FavoriteRepository;
import com.example.whereshouldwego.repository.postgres.UserRepository;
import com.example.whereshouldwego.repository.postgres.PlaceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService {
    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;
    private final FavoriteRepository favoriteRepository;

    public CreateFavoriteResponse createFavorite(CreateFavoriteRequest request){
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다. "));
        Place place = placeRepository.findById(request.getPlaceId())
                .orElseThrow(() -> new IllegalArgumentException("해당 장소가 존재하지 않습니다."));
        Favorite favorite = Favorite.builder()
                .user(user)
                .place(place)
                .build();
        Favorite saved = favoriteRepository.save(favorite);
        return convertToDto(saved);
    }

    // 특정 사용자의 찜 목록 조회
    public List<CreateFavoriteResponse> findByUserId(Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));
        return favoriteRepository.findByUserId(userId)
                .stream()
                .map(this::convertToDto)
                .toList();

    }
    private CreateFavoriteResponse convertToDto(Favorite favorite){
        return CreateFavoriteResponse.builder()
                .favoriteId(favorite.getId())
                .userId(favorite.getUser().getId())
                .placeId(favorite.getPlace().getId())
                .build();
    }

    // 특정 찜 정보 조회
    @Transactional
    public void delete(Long id){
        Favorite favorite = favoriteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 찜 정보가 존재하지 않습니다."));

        favoriteRepository.delete(favorite);
    }

}
