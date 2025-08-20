package com.example.whereshouldwego.realtime.config;

import com.example.whereshouldwego.realtime.handler.CursorWebSocketHandler;
import com.example.whereshouldwego.auth.security.ws.RawWebSocketAuthInterceptor;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@AllArgsConstructor
public class RawWebSocketConfig implements WebSocketConfigurer {

    private final CursorWebSocketHandler handler;
    private final RawWebSocketAuthInterceptor webSocketAuthInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(handler, "/ws-raw/cursor")
                .addInterceptors(webSocketAuthInterceptor)
                .setAllowedOriginPatterns("*");
    }
}
