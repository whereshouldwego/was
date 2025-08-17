package com.example.whereshouldwego.repository.postgres;

import com.example.whereshouldwego.domain.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    List<Recommendation> findByRoomId(Long roomId);
}