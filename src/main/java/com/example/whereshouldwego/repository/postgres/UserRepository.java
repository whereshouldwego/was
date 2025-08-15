package com.example.whereshouldwego.repository.postgres;

import com.example.whereshouldwego.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    @Query("select u.id from User u where u.username = :username")
    Optional<Long> findIdByUsername(String username);
}