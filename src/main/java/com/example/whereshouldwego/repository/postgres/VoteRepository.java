package com.example.whereshouldwego.repository.postgres;

import com.example.whereshouldwego.domain.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    boolean existsByRoomCodeAndUserIdAndPlaceId(String roomCode, Long userId, Long placeId);

    void deleteByRoomCodeAndUserIdAndPlaceId(String roomCode, Long userId, Long placeId);

    void deleteByRoomCodeAndPlaceId(String roomCode, Long placeId);

    @Query("select v.userId from Vote v where v.roomCode = :roomCode and v.placeId = :placeId")
    List<Long> findVotedUserIds(String roomCode, Long placeId);

}
