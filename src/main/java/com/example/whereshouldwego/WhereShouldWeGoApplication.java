package com.example.whereshouldwego;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "com.example.whereshouldwego.repository.postgres")
public class WhereShouldWeGoApplication {

	public static void main(String[] args) {
		SpringApplication.run(WhereShouldWeGoApplication.class, args);
	}

}
