package com.example.whereshouldwego.repository;

import com.example.whereshouldwego.domain.Room;
import com.example.whereshouldwego.domain.RoomParticipant;
import com.example.whereshouldwego.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomParticipantRepository extends JpaRepository<RoomParticipant, Long> {

    Optional<RoomParticipant> findByRoomIdAndUserId(User user, Room room);
}
