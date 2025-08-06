package com.example.whereshouldwego.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "room_participants")
public class RoomParticipant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // userId로 할 경우에는 이게 user 객체를 참조하는 걸 인식 못함

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false, updatable = false)
    private LocalDateTime participatedAt;

    private String startLocation;


    @PrePersist
    protected void onCreate() {

        this.participatedAt = LocalDateTime.now();
    }
}
