package com.example.whereshouldwego.service;

import com.example.whereshouldwego.domain.Room;
import com.example.whereshouldwego.domain.RoomParticipant;
import com.example.whereshouldwego.domain.User;
import com.example.whereshouldwego.dto.CreateRoomRequest;
import com.example.whereshouldwego.dto.CreateRoomResponse;
import com.example.whereshouldwego.dto.RoomResponse;
import com.example.whereshouldwego.repository.RoomParticipantRepository;
import com.example.whereshouldwego.repository.RoomRepository;
import com.example.whereshouldwego.repository.UserRepository;
import org.aspectj.lang.annotation.Before;
import org.hibernate.annotations.SQLJoinTableRestriction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class RoomServiceTest {
    @MockitoBean
    private RoomRepository roomRepository;
    @MockitoBean
    private RoomParticipantRepository roomParticipantRepository;
    @MockitoBean
    private UserRepository userRepository;

    @Autowired
    private RoomService roomService;

    private User mockUser; // 임시 사용자 설정
    private Room mockRoom; // 방 객체 생성

    @BeforeEach
    void setUp(){
        mockUser = User.builder().id(1L).build();
        mockRoom = Room.builder().id(10L).user(mockUser).build();
    }

//    @Test
//    @DisplayName("비회원이 방을 생성하는 경우")
//    public void createRoom_withoutUserId(){
//        when()
//    }


}
