package com.example.whereshouldwego.features.user.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String role;

    private String name;
    private String email;
    private String image;

    public void updateToSocialUser(String username, String role, String name, String email, String image) {
        this.username = username;
        this.role = role;
        this.name = name;
        this.email = email;
        this.image = image;
    }
}
