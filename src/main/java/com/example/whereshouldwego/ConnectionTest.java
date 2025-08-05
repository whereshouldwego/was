package com.example.whereshouldwego;


import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;

@Configuration
public class ConnectionTest {

    @Bean
    CommandLineRunner testConnection() {
        return args -> {
            String url = "jdbc:postgresql://b110-postgresql.cdykwggiysrn.ap-northeast-2.rds.amazonaws.com:5432/postgres";
            String username = "postgres";
            String password = "b110harry";

            try (Connection conn = DriverManager.getConnection(url, username, password)) {
                System.out.println("✅✅✅ RDS 연결 성공!");
            } catch (Exception e) {
                System.err.println("❌ RDS 연결 실패: " + e.getMessage());
                e.printStackTrace();
            }
        };
    }
}