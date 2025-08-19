package com.example.whereshouldwego.config;

import com.example.whereshouldwego.messaging.LoggingMdcInterceptor;
import com.example.whereshouldwego.messaging.StompAuthInterceptor;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@AllArgsConstructor
public class StompWebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompAuthInterceptor stompAuthInterceptor;
    private final LoggingMdcInterceptor loggingMdcInterceptor;

    // Simple Broker 설정
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 구독 경로
        registry.enableSimpleBroker("/topic");

        // 발행 경로
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // STOMP endpoint
        registry.addEndpoint("/ws-stomp")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.taskExecutor()
                .corePoolSize(4)
                .maxPoolSize(16)
                .queueCapacity(1000);

        registration.interceptors(stompAuthInterceptor)     // Token 검증 Interceptor
                .interceptors(loggingMdcInterceptor);   // Logging Interceptor
    }
}
