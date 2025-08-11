package com.example.whereshouldwego.repository.postgres;

import com.example.whereshouldwego.domain.Room;
import com.example.whereshouldwego.domain.RoomParticipant;
import com.example.whereshouldwego.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomParticipantRepository extends JpaRepository<RoomParticipant, Long> {

    Optional<RoomParticipant> findByRoomAndUser(Room room, User user);
    Boolean existsByRoomAndNickname(Room room, String nickname);
    List<RoomParticipant> findAllByUser(User user);
}
