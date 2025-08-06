package com.example.whereshouldwego.repository;

import com.example.whereshouldwego.domain.Favorite;
import com.example.whereshouldwego.domain.User;
import com.example.whereshouldwego.domain.secondary.Place;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class FavoriteRepositoryTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private FavoriteRepository favoriteRepository;
    @Autowired
    private PlaceRepository placeRepository;

    private User testUser;
    private Place testPlace;

    @BeforeEach
    void setUp(){
        testUser = User.builder()
                .build();
        userRepository.save(testUser);

        testPlace = placeRepository.findById(29633L)
                .orElseThrow(() -> new IllegalArgumentException("테스트용 Place가 존재하지 않습니다."));
    }
    @Test
    @DisplayName("찜 저장 및 조회 테스트")
    public void saveAndFindFavorite(){
        Favorite favorite  = Favorite.builder()
                .user(testUser)
                .place(testPlace)
                .build();

        Favorite saved = favoriteRepository.save(favorite);
        Optional<Favorite> found = favoriteRepository.findById(saved.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getUser().getId()).isEqualTo(testUser.getId());
        assertThat(found.get().getPlace().getId()).isEqualTo(testPlace.getId());

        System.out.println("Saved Favorite: " + saved);
        System.out.println("Found Favorite: " + found.orElse(null));
        System.out.println("Found Favorite UserId: " + saved.getUser().getId());
        System.out.println("Found Favorite PlaceId: " + saved.getPlace().getName());
    }
    @Test
    @DisplayName("찜 삭제 테스트")
    public void deleteFavorite(){
        Favorite favorite = Favorite.builder()
                .user(testUser)
                .place(testPlace)
                .build();
        Favorite saved = favoriteRepository.save(favorite);

        Long id = saved.getId();

        favoriteRepository.deleteById(id);

        Optional<Favorite> found = favoriteRepository.findById(id);
        assertThat(found).isEmpty();
    }
}
