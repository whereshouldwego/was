package com.example.whereshouldwego.repository.postgres;

import com.example.whereshouldwego.domain.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    boolean existsByRoomIdAndUserIdAndPlaceId(Long roomId, Long userId, Long placeId);

    void deleteByRoomIdAndUserIdAndPlaceId(Long roomId, Long userId, Long placeId);

    void deleteByRoomIdAndPlaceId(Long roomId, Long placeId);

    @Query("""
        SELECT v.userId 
        FROM Vote v 
        WHERE v.roomId = :roomId AND v.placeId = :placeId
    """)
    List<Long> findVotedUserIds(Long roomId, Long placeId);

}
