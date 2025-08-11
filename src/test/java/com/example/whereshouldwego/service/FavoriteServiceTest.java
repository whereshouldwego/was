package com.example.whereshouldwego.service;

import com.example.whereshouldwego.config.TestJwtConfig;
import com.example.whereshouldwego.domain.User;
import com.example.whereshouldwego.domain.Place;
import com.example.whereshouldwego.dto.request.CreateFavoriteRequest;
import com.example.whereshouldwego.dto.response.CreateFavoriteResponse;
import com.example.whereshouldwego.jwt.JWTUtil;
import com.example.whereshouldwego.repository.postgres.FavoriteRepository;
import com.example.whereshouldwego.repository.postgres.PlaceRepository;
import com.example.whereshouldwego.repository.postgres.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc(addFilters=false)
public class FavoriteServiceTest {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PlaceRepository placeRepository;
    @Autowired
    private FavoriteRepository favoriteRepository;
    @Autowired
    private FavoriteService favoriteService;

    private User testUser;
    private Place testPlace;
    @BeforeEach
    public void setUp(){
        testUser = userRepository.findById(11L).get();
        userRepository.save(testUser);

        testPlace = placeRepository.findById(29633L)
                .orElseThrow(() -> new IllegalArgumentException("해당 장소가 존재하지 않습니다."));
    }

//    @AfterEach
//    @Disabled
//    public void doAfterEach(){
//        favoriteRepository.deleteAll();
//        userRepository.deleteAll();
//    }

    @Test
    @DisplayName("1. 찜 등록 성공")
    public void testCreateFavoriteSuccess(){
        CreateFavoriteRequest request = new CreateFavoriteRequest(testUser.getId(), testPlace.getId());
        CreateFavoriteResponse response = favoriteService.createFavorite(request);

        assertThat(response.getFavoriteId()).isNotNull();
        assertThat(response.getUserId()).isEqualTo(testUser.getId());
        assertThat(response.getPlaceId()).isEqualTo(testPlace.getId());
        }

    @Test
    @DisplayName("2. 찜 등록 - 존재하지 않는 사용자")
    public void testCreateFavoriteFail(){
        CreateFavoriteRequest request = new CreateFavoriteRequest(9999L, testPlace.getId());
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {favoriteService.createFavorite(request);});
        assertEquals("해당 사용자가 존재하지 않습니다. ", exception.getMessage());
    }
    @Test
    @DisplayName("3. 찜 등록 - 존재하지 않는 장소 예외")
    public void testCreateFavoritePlaceNotFound(){
        CreateFavoriteRequest request = new CreateFavoriteRequest(testUser.getId(), 1L);
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {favoriteService.createFavorite(request);});
        assertEquals("해당 장소가 존재하지 않습니다.", exception.getMessage());
    }
    @Test
    @DisplayName("4. 찜 목록 조회 - 찜이 있을 때")
    public void testFindFavoriteByUserId(){
        CreateFavoriteRequest request = new CreateFavoriteRequest(testUser.getId(), testPlace.getId());
        favoriteService.createFavorite(request);
        List<CreateFavoriteResponse> favorites = favoriteService.findByUserId(testUser.getId());
        assertThat(favorites.size()).isEqualTo(1);
        assertThat(testPlace.getId()).isEqualTo(favorites.get(0).getPlaceId());
    }
    @Test
    @DisplayName("5. 찜 목록 조회 - 존재하지 않는 사용자")
    public void testFindByUserIdUserNotFound(){
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            favoriteService.findByUserId(99999L);
        });
        assertEquals("해당 사용자가 존재하지 않습니다.", exception.getMessage());
    }
    @Test
    @DisplayName("7. 찜 삭제 - 성공")
    public void testDeleteSuccess(){
        CreateFavoriteRequest request = new CreateFavoriteRequest(testUser.getId(), testPlace.getId());
        CreateFavoriteResponse response = favoriteService.createFavorite(request);

        favoriteService.delete(response.getFavoriteId());
        assertThat(favoriteRepository.findById(response.getFavoriteId())).isNotPresent();
    }
    @Test
    @DisplayName("8. 찜 삭제 - 찜 정보가 없는 삭제")
    public void testDeleteNotFound() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
        {favoriteService.delete(10000000L);});
        assertEquals("해당 찜 정보가 존재하지 않습니다.",exception.getMessage());
    }

}
