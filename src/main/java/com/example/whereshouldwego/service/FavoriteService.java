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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final UserRepository userRepository;
    private final PlaceRepository placeRepository;
    private final FavoriteRepository favoriteRepository;

    // 특정 사용자의 찜 추가
    @Transactional
    public CreateFavoriteResponse createFavorite(CreateFavoriteRequest request, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));

        Place place = placeRepository.getReferenceById(request.getPlaceId());

        Favorite favorite = favoriteRepository
                .findByUser_UsernameAndPlace_Id(username, place.getId())
                .orElseGet(() -> favoriteRepository.save(Favorite.of(user, place)));

        return CreateFavoriteResponse.from(favorite);
    }

    // 특정 사용자의 찜 목록 조회
    public List<CreateFavoriteResponse> findMyFavorites(String username) {
        return favoriteRepository.findByUser_Username(username).stream()
                .map(CreateFavoriteResponse::from)
                .toList();
    }

    // 특정 찜 정보 조회
    @Transactional
    public void deleteFavorite(Long favoriteId, String username) {
        Favorite favorite = favoriteRepository.findById(favoriteId)
                .orElseThrow(() -> new IllegalArgumentException("해당 찜 정보가 존재하지 않습니다."));

        if (!favorite.getUser().getUsername().equals(username)) {
            throw new AccessDeniedException("본인의 찜만 삭제할 수 있습니다.");
        }
        favoriteRepository.delete(favorite);
    }
}
