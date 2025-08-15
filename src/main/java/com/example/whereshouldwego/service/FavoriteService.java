package com.example.whereshouldwego.service;

import com.example.whereshouldwego.domain.Favorite;
import com.example.whereshouldwego.domain.User;
import com.example.whereshouldwego.domain.Place;
import com.example.whereshouldwego.dto.request.CreateFavoriteRequest;
import com.example.whereshouldwego.dto.response.CreateFavoriteResponse;
import com.example.whereshouldwego.dto.response.CustomUserDetails;
import com.example.whereshouldwego.repository.postgres.FavoriteRepository;
import com.example.whereshouldwego.repository.postgres.UserRepository;
import com.example.whereshouldwego.repository.postgres.PlaceRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService {
    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;
    private final FavoriteRepository favoriteRepository;
    @Transactional
    public CreateFavoriteResponse createFavorite(CreateFavoriteRequest request, CustomUserDetails userDetails) {

        String username = userDetails.getUsername();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다. "));

        Place place;
        if (request.getPlaceId() != null){
            Long id = request.getPlaceId();
            place = placeRepository.findById(id)
                    .orElseGet(()->{
                        Place p = Place.builder()
                                .id(id)
                                .name(request.getName())
                                .kakaoUrl(request.getKakaoUrl())
                                .x(request.getX())
                                .y(request.getY())
                                .address(request.getAddress())
                                .roadAddress(request.getRoadAddress())
                                .phone(request.getPhone())
                                .categoryCode(request.getCategoryCode())
                                .categoryName(request.getCategoryName())
                                .build();
                        try {
                            return placeRepository.save(p);
                        }catch(DataIntegrityViolationException e){
                            return placeRepository.findById(request.getPlaceId())
                                    .orElseThrow(() ->e);
                        }
                    });
        } else {
            if (request.getKakaoPlaceId() == null){
                throw new IllegalArgumentException("장소 id가 누락된 잘못된 요청입니다.");
            }
            place = placeRepository.findById(request.getKakaoPlaceId())
                    .orElseGet(() -> {
                        Place newPlace = Place.builder()
                                .id(request.getKakaoPlaceId())
                                .name(request.getName())
                                .kakaoUrl(request.getKakaoUrl())
                                .x(request.getX())
                                .y(request.getY())
                                .address(request.getAddress())
                                .roadAddress(request.getRoadAddress())
                                .phone(request.getPhone())
                                .categoryCode(request.getCategoryCode())
                                .categoryName(request.getCategoryName())
                                .build();
                        try {
                            return placeRepository.save(newPlace);
                        }catch(DataIntegrityViolationException e){
                            return placeRepository.findById(request.getKakaoPlaceId())
                                    .orElseThrow(() ->e);
                        }
                    });
        }
        boolean exists = favoriteRepository.existsByUser_IdAndPlace_Id(user.getId(), place.getId());
        Favorite saved = exists
                ? favoriteRepository.findByUser_IdAndPlace_Id(user.getId(), place.getId()).get()
                : favoriteRepository.save(Favorite.builder().user(user).place(place).build());
            return convertToDto(saved);
    }

    // 특정 사용자의 찜 목록 조회
    public List<CreateFavoriteResponse> findByUserId(CustomUserDetails userDetails){

        String username = userDetails.getUsername();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));

        return favoriteRepository.findByUserId(user.getId())
                .stream()
                .map(this::convertToDto)
                .toList();
    }
    private CreateFavoriteResponse convertToDto(Favorite favorite) {
        return CreateFavoriteResponse.builder()
                .favoriteId(favorite.getId())
                .userId(favorite.getUser().getId())
                .placeId(favorite.getPlace().getId())
                .build();
    }

    // 특정 찜 정보 조회
    @Transactional
    public void delete(Long id, CustomUserDetails userDetails){
        Favorite favorite = favoriteRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 찜 정보가 존재하지 않습니다."));

        favoriteRepository.delete(favorite);
    }

}
