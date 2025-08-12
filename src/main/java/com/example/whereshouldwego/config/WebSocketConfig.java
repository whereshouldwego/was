package com.example.whereshouldwego.config;

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
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    private final StompAuthInterceptor stompAuthInterceptor;

    /*
    // 외부 Broker(RabbitMQ) 용 설정
    @Value("${rabbitmq.host}")
    private String rabbitmqHost;

    @Value("${rabbitmq.client.login}")
    private String rabbitmqClientLogin;

    @Value("${rabbitmq.client.passcode}")
    private String rabbitmqClientPasscode;

    @Value("${rabbitmq.server.login}")
    private String rabbitmqServerLogin;

    @Value("${rabbitmq.server.passcode}")
    private String rabbitmqServerPasscode;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableStompBrokerRelay("/topic", "/queue")
                .setRelayHost(rabbitmqHost)
                .setRelayPort(61613) // 포트 번호는 일반적으로 고정됩니다.
                .setClientLogin(rabbitmqClientLogin)
                .setClientPasscode(rabbitmqClientPasscode)
                .setSystemLogin(rabbitmqServerLogin)
                .setSystemPasscode(rabbitmqServerPasscode)
                .setVirtualHost("/");

        registry.setApplicationDestinationPrefixes("/ws/send");
    }
     */

    // Simple Broker 용 설정
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/topic", "/queue");

        registry.setApplicationDestinationPrefixes("/ws");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // STOMP endpoint
        registry
                .addEndpoint("/ws")
                .setAllowedOriginPatterns("*")
                .withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompAuthInterceptor);
    }
}
