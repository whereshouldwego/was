package com.example.whereshouldwego;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
//@EnableJpaRepositories(basePackages = "com.example.whereshouldwego.repository.secondary")
//@EnableMongoRepositories(basePackages = "com.example.whereshouldwego.repository.mongo")
public class WhereShouldWeGoApplication {

	public static void main(String[] args) {
		SpringApplication.run(WhereShouldWeGoApplication.class, args);
	}

}
