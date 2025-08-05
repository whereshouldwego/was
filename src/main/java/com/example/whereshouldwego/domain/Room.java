package com.example.whereshouldwego.domain;


import com.example.whereshouldwego.util.RoomCodeUtil;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name="rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Column(name="code", unique=true, nullable = true, length = 6)
    private String roomCode;
    @Column(name="created_at")
    private LocalDateTime createdAt;
    @Column(name="expired_at")
    private LocalDateTime expiredAt;
    @Column(name="url", unique = true)
    private String roomUrl;
    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;
    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }



}
