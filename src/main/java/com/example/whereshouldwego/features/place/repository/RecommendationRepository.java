package com.example.whereshouldwego.features.place.repository;

import com.example.whereshouldwego.features.place.domain.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    List<Recommendation> findByRoomId(Long roomId);
}