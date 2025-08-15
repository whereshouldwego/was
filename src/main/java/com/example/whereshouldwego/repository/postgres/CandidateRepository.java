package com.example.whereshouldwego.repository.postgres;

import com.example.whereshouldwego.domain.Candidate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidateRepository extends JpaRepository<Candidate, Long> {

    void deleteByRoomIdAndPlaceId(Long roomId, Long placeId);

    @Query("""
        select p, count(v.id)
        from Place p
        join fetch Candidate c on c.placeId = p.id
        left join Vote v on v.placeId = p.id and v.roomId = c.roomId
        where c.roomId = :roomId
        group by p
        order by count(v.id) desc
    """)
    List<Object[]> findPlacesWithVoteCountFetchJoin(@Param("roomId") Long roomId);
}
