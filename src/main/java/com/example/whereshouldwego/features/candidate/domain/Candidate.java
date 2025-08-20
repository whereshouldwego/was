package com.example.whereshouldwego.features.candidate.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@Table(name = "candidates")
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Candidate {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "room_id",  nullable = false)
    private Long roomId;

    @Column(name = "place_id", nullable = false)
    private Long placeId;

    public static Candidate of(long roomId, long placeId) {
        return Candidate.builder()
                .roomId(roomId)
                .placeId(placeId)
                .build();
    }
}