//package com.example.whereshouldwego.service;
//
//import com.example.whereshouldwego.domain.Room;
//import com.example.whereshouldwego.domain.User;
//import com.example.whereshouldwego.repository.postgres.RoomParticipantRepository;
//import com.example.whereshouldwego.repository.postgres.RoomRepository;
//
//import com.example.whereshouldwego.repository.postgres.UserRepository;
//
//import org.junit.jupiter.api.BeforeEach;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.bean.override.mockito.MockitoBean;
//
//import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
//import static org.mockito.ArgumentMatchers.any;
//
//@SpringBootTest
//public class RoomServiceTest {
//    @MockitoBean
//    private RoomRepository roomRepository;
//    @MockitoBean
//    private RoomParticipantRepository roomParticipantRepository;
//    @MockitoBean
//    private UserRepository userRepository;
//
//    @Autowired
//    private RoomService roomService;
//
//    private User mockUser; // 임시 사용자 설정
//    private Room mockRoom; // 방 객체 생성
//
//    @BeforeEach
//    void setUp(){
//        mockUser = User.builder().id(1L).build();
//        mockRoom = Room.builder().id(10L).user(mockUser).build();
//    }
//
////    @Test
////    @DisplayName("비회원이 방을 생성하는 경우")
////    public void createRoom_withoutUserId(){
////        when()
////    }
//
//
//}
