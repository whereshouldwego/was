package com.example.whereshouldwego.repository.postgres;

import com.example.whereshouldwego.domain.CandidateMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidateRepository extends JpaRepository<CandidateMessage, Long> {

    boolean existsByRoomIdAndPlaceId(Long roomId, Long placeId);
    void deleteByRoomIdAndPlaceId(Long roomId, Long placeId);

    @Query(value = """
        SELECT c.place_id
        FROM candidates c
        LEFT JOIN votes v ON c.place_id = v.place_id AND c.room_id = v.room_id
        WHERE c.room_id = :roomId
        GROUP BY c.place_id
        ORDER BY COUNT(v.id) DESC
    """, nativeQuery = true)
    List<Long> findCandidatePlaceIdsOrderByVoteCount(Long roomId);

}
