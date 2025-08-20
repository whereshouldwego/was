package com.example.whereshouldwego.features.candidate.repository;

import com.example.whereshouldwego.features.candidate.domain.Vote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface VoteRepository extends JpaRepository<Vote, Long> {

    void deleteByRoomIdAndPlaceId(Long roomId, Long placeId);

    void deleteByRoomIdAndUserIdAndPlaceId(Long roomId, Long userId, Long placeId);

    @Modifying
    @Query("UPDATE Vote v SET v.userId = :newUserId WHERE v.userId = :oldUserId")
    void updateMemberIdByGuestId(@Param("oldUserId") Long oldUserId, @Param("newUserId") Long newUserId);
}
