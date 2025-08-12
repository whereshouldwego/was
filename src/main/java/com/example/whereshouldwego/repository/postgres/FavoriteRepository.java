package com.example.whereshouldwego.repository.postgres;

import com.example.whereshouldwego.domain.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUserId(Long userId);

    Optional<Favorite> findByUser_IdAndPlace_Id(Long userId, Long placeId);
    boolean existsByUser_IdAndPlace_Id(Long userId, Long placeId);
}
