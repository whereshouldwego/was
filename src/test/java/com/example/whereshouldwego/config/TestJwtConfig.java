package com.example.whereshouldwego.config;

import com.example.whereshouldwego.auth.security.jwt.JwtUtil;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestJwtConfig {

    @Bean
    public JwtUtil jwtUtil(){
        return mock(JwtUtil.class);
    }
}
