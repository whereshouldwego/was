//package com.example.whereshouldwego.controller;
//
//import com.example.whereshouldwego.domain.Favorite;
//import com.example.whereshouldwego.domain.User;
//import com.example.whereshouldwego.domain.Place;
//import com.example.whereshouldwego.dto.request.CreateFavoriteRequest;
//import com.example.whereshouldwego.repository.postgres.FavoriteRepository;
//import com.example.whereshouldwego.repository.postgres.PlaceRepository;
//import com.example.whereshouldwego.repository.postgres.UserRepository;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.web.servlet.MockMvc;
//
//
//import java.util.Optional;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@AutoConfigureMockMvc(addFilters = false)
//@SpringBootTest
//@ActiveProfiles("test")
//public class FavoriteControllerTest {
//    @Autowired
//    private MockMvc mockMvc;
//    @Autowired
//    private ObjectMapper objectMapper;
//    @Autowired
//    private UserRepository userRepository;
//    @Autowired
//    private PlaceRepository placeRepository;
//    @Autowired
//    private FavoriteRepository favoriteRepository;
//
//    private User user;
//    private Place place;
//    @BeforeEach
//    public void setUp(){
////        favoriteRepository.deleteAll();
////        userRepository.deleteAll();
//        user= userRepository.findById(11L).get();
//        place = placeRepository.findById(29633L)
//                .orElseThrow(() -> new IllegalArgumentException("해당 장소가 존재하지 않습니다."));
//
//
//    }
//    @Test
//    @DisplayName("찜 생성 성공")
//    void createFavoriteTest() throws Exception{
//        CreateFavoriteRequest request =new CreateFavoriteRequest(user.getId(), place.getId());
//        String requestBody = objectMapper.writeValueAsString(request);
//        mockMvc.perform(post("/api/favorites").contentType(MediaType.APPLICATION_JSON)
//                .content(requestBody))
//                .andDo(print())
//                .andExpect(status().isCreated());
//    }
//
//    @Test
//    @DisplayName("찜 조회 성공")
//    public void findByUserIdTest() throws Exception{
//        favoriteRepository.save(Favorite.builder().user(user).place(place).build());
//        mockMvc.perform(get("/api/favorites/{userId}", user.getId()).contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isOk());
//
//    }
//
//    @Test
//    @DisplayName("찜 삭제 성공")
//    public void deleteFavorite() throws Exception{
//        Favorite favorite = favoriteRepository.save(
//                Favorite.builder().user(user).place(place).build());
//        mockMvc.perform(delete("/api/favorites/{favoriteId}", favorite.getId())
//                .contentType(MediaType.APPLICATION_JSON))
//                .andDo(print())
//                .andExpect(status().isNoContent());
//    }
//
//
//
//}
