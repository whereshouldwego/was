package com.example.whereshouldwego.service;

import com.example.whereshouldwego.domain.Room;
import com.example.whereshouldwego.domain.User;
import com.example.whereshouldwego.dto.CreateRoomRequest;
import com.example.whereshouldwego.dto.CreateRoomResponse;
import com.example.whereshouldwego.dto.RoomResponse;
import com.example.whereshouldwego.repository.RoomRepository;
import com.example.whereshouldwego.repository.UserRepository;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class RoomServiceTest {
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    RoomService roomService;

    private User savedUser;

    @BeforeEach
    public void setUp() {
        // 테스트용 사용자 저장
        savedUser = userRepository.save(new User());
    }
    @Test
    public void testCreateRoom_withExistingUser() {
        CreateRoomRequest request = CreateRoomRequest.builder()
                .userId(savedUser.getId())
                .build();
        CreateRoomResponse response = roomService.createRoom(request);

        assertThat(response.getRoomCode()).isNotNull();
        assertThat(response.getRoomUrl()).contains(response.getRoomCode());

    }
    @Test
    public void testCreateRoom_withoutUserId() {
        CreateRoomRequest request = CreateRoomRequest.builder()
                .userId(null)
                .build();

        CreateRoomResponse response = roomService.createRoom(request);

        assertThat(response.getRoomCode()).isNotNull();
        assertThat(response.getRoomUrl()).contains(response.getRoomCode());
    }
    @Test
    public void testGetRoomByCode() {
        Room room = Room.builder()
                .user(savedUser)
                .roomCode("ABC123")
                .roomUrl("https://localhost:8080/ABC123")
                .build();
        roomRepository.save(room);

        RoomResponse found = roomService.getRoomByCode("ABC123");

        assertThat(found).isNotNull();
        assertThat(found.getRoomCode()).isEqualTo("ABC123");

    }
}
