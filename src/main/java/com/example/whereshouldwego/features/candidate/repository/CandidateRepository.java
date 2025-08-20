package com.example.whereshouldwego.features.candidate.repository;

import com.example.whereshouldwego.features.candidate.domain.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {
    boolean existsByRoomIdAndPlaceId(Long roomId, Long placeId);

    void deleteByRoomIdAndPlaceId(Long roomId, Long placeId);

    @Query("""
        select p, count(v.id)
        from Candidate c
        join Place p on p.id = c.placeId
        left join Vote v on v.placeId = p.id and v.roomId = c.roomId
        where c.roomId = :roomId
        group by p
        order by count(v.id) desc
    """)
    List<Object[]> findPlacesWithVoteCountFetchJoin(@Param("roomId") Long roomId);
}
