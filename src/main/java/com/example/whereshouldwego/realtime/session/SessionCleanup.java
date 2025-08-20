package com.example.whereshouldwego.realtime.session;

import com.example.whereshouldwego.auth.security.ws.StompAuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
public class SessionCleanup implements ApplicationListener<SessionDisconnectEvent> {
    private final StompAuthInterceptor auth;
    private final RawWebSocketSessionRegistry rawWebSocketSessionRegistry;

    @Override
    public void onApplicationEvent(SessionDisconnectEvent e) {
        String sid = e.getSessionId();

        Long roomId = auth.getSessionCurrentRoom().remove(sid);
        Long userId = auth.getSessionUserId().remove(sid);
        auth.getSessionSubRoom().remove(sid);

        if (roomId != null && userId != null) {
            rawWebSocketSessionRegistry.closeIfPresent(roomId, userId);
        }
    }
}