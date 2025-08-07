package com.example.whereshouldwego.config;

import com.example.whereshouldwego.jwt.JWTUtil;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestJwtConfig {

    @Bean
    public JWTUtil jwtUtil(){
        return mock(JWTUtil.class);
    }
}
