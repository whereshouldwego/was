package com.example.whereshouldwego.repository.postgres;

import com.example.whereshouldwego.domain.secondary.Place;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaceRepository extends JpaRepository<Place, Long> {

}
