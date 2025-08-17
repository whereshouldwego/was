package com.example.whereshouldwego.messaging;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
public class StompSessionCleanup implements ApplicationListener<SessionDisconnectEvent> {
    private final StompAuthInterceptor auth;

    @Override
    public void onApplicationEvent(SessionDisconnectEvent e) {
        auth.getSessionRooms().remove(e.getSessionId());
    }
}