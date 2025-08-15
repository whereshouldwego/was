package com.example.whereshouldwego.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Getter
@Builder
@Table(name = "candidates")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Candidate {

    @Id
    @GeneratedValue
    private Long id;
    private Long roomId;
    private Long placeId;

    public static Candidate of(long roomId, long placeId) {
        return Candidate.builder()
                .roomId(roomId)
                .placeId(placeId)
                .build();
    }
}

