package com.example.whereshouldwego.repository;

import com.example.whereshouldwego.domain.Room;
import com.example.whereshouldwego.domain.User;
import com.example.whereshouldwego.util.RoomCodeUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class RoomRepositoryTest {
    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    private Room testRoom;
    private User testUser;
    private Room testRoom2;
    // 테스트 시작 전 userRepository와 roomRepository에 테스트 데이터 삽입
    @BeforeEach
    public void doBeforeEach(){
        testUser = User.builder()
                .build();
        userRepository.save(testUser);

        testRoom = Room.builder()
                        .build();
        roomRepository.save(testRoom);
        testRoom.setUser(testUser);

        String roomCode = RoomCodeUtil.encode(testRoom.getId());
        String roomUrl = "http://localhost:8080/" + roomCode;

        testRoom.setRoomCode(roomCode);
        testRoom.setRoomUrl(roomUrl);
        roomRepository.save(testRoom);

        testRoom2 = Room.builder().build();
        roomRepository.save(testRoom2);

        String roomCode2 = RoomCodeUtil.encode(testRoom2.getId());
        String roomUrl2=  "http://localhost:8080/"+ roomCode2;

        testRoom2.setRoomCode(roomCode2);
        testRoom2.setRoomUrl(roomUrl2);
        roomRepository.save(testRoom2);
    }
    @AfterEach
    public void doAfterEach() {
        roomRepository.deleteAll();
    }
    @Test
    @DisplayName("Room이 정상적으로 생성되는지 확인")
    public void testRoom(){
        Optional<Room> foundRoom = roomRepository.findByRoomCode(testRoom.getRoomCode());
        Room room = foundRoom.get();
        assertThat(foundRoom).isPresent();
        assertThat(room.getUser().getId()).isEqualTo(testUser.getId());
        assertThat(room.getRoomCode()).isEqualTo(testRoom.getRoomCode());
        assertThat(room.getRoomUrl()).isNotBlank();
        assertThat(room.getCreatedAt()).isNotNull();

        System.out.println("Room Id:" + testRoom.getId());
        System.out.println("Room Code:"  + testRoom.getRoomCode());
        System.out.println("Room URL: " + testRoom.getRoomUrl());
        System.out.println("Room CreatedAt" + testRoom.getCreatedAt());
        System.out.println("Room ExpiredAt" + testRoom.getExpiredAt());

    }
    @Test
    @DisplayName("방 코드가 중복되지는 않는지 확인")
    public void uniqueRoomCode(){
        String roomCode1 = testRoom.getRoomCode();
        String roomCode2 = testRoom2.getRoomCode();

        System.out.println("Room1 Code: " + roomCode1);
        System.out.println("Room2 Code: " + roomCode2);

        assertThat(testRoom.getRoomCode()).isNotEqualTo(testRoom2.getRoomCode());
    }

}
