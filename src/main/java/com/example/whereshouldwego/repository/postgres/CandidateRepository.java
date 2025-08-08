package com.example.whereshouldwego.repository.postgres;

import com.example.whereshouldwego.domain.CandidateMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CandidateRepository extends JpaRepository<CandidateMessage, Long> {
    boolean existsByRoomCodeAndPlaceId(String roomCode, Long placeId);
    void deleteByRoomCodeAndPlaceId(String roomCode, Long placeId);
}
