package com.example.whereshouldwego.domain;


import com.example.whereshouldwego.util.RoomCodeUtil;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "rooms")
public class Room {

    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;

    @Column(name = "code", unique = true, length = 6)
    private String roomCode;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    private LocalDateTime expiredAt;

    @Column(name = "url", unique = true)
    private String roomUrl;

    @PrePersist
    protected void onCreate() {

        this.createdAt = LocalDateTime.now();
    }
}
