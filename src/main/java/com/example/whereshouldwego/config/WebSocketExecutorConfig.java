package com.example.whereshouldwego.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class WebSocketExecutorConfig {

    @Bean(name = "websocketTaskExecutor")
    public ThreadPoolTaskExecutor websocketTaskExecutor() {
        ThreadPoolTaskExecutor ex = new ThreadPoolTaskExecutor();
        ex.setThreadNamePrefix("ws-raw-");
        ex.setCorePoolSize(4);
        ex.setMaxPoolSize(16);
        ex.setQueueCapacity(1000);
        ex.setWaitForTasksToCompleteOnShutdown(true);
        ex.initialize();
        return ex;
    }
}