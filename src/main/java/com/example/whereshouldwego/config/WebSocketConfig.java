package com.example.whereshouldwego.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        // 클라이언트가 구독할 수 있는 주소
        config.enableSimpleBroker("/ws/subscribe");

        // 클라이언트가 서버로 메시지를 보낼 때 사용하는 주소
        config.setApplicationDestinationPrefixes("/ws/send");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // STOMP 웹소켓 연결 endpoint
        registry.addEndpoint("/ws").withSockJS();
    }
}
