package com.example.whereshouldwego.repository.postgres;

import com.example.whereshouldwego.domain.Room;
import com.example.whereshouldwego.domain.RoomParticipant;
import com.example.whereshouldwego.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomParticipantRepository extends JpaRepository<RoomParticipant, Long> {

    Optional<RoomParticipant> findByRoomAndUser(Room room, User user);
    List<RoomParticipant> findAllByRoom(Room room);

    boolean existsByRoomIdAndUserId(Long roomId, Long userId);

    @Modifying
    @Query("UPDATE RoomParticipant rp SET rp.user.id = :newUserId WHERE rp.user.id = :oldUserId")
    void updateMemberIdByGuestId(@Param("oldUserId") Long oldUserId, @Param("newUserId") Long newUserId);
}
