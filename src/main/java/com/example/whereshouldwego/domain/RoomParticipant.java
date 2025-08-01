package com.example.whereshouldwego.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name ="room_participants")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RoomParticipant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne
    @JoinColumn(name="user_id", nullable=false)
    private User user; // userId로 할 경우에는 이게 user 객체를 참조하는 걸 인식 못함

    @ManyToOne
    @JoinColumn(name="room_id",nullable=false)
    private Room room;

    @Column(name="participated_at", nullable=false)
    private LocalDateTime participatedAt;

    @Column(name="start_location", nullable = true)
    private String startLocation;

}
