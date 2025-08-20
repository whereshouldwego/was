package com.example.whereshouldwego.realtime.session;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;

import java.util.concurrent.ConcurrentHashMap;

@Component
public class RawWebSocketSessionRegistry {

    // roomId:userId
    private final ConcurrentHashMap<String, WebSocketSession> byPair = new ConcurrentHashMap<>();
    // sessionId -> key
    private final ConcurrentHashMap<String, String> pairBySessionId = new ConcurrentHashMap<>();

    private static String key(Long roomId, Long userId) {
        return roomId + ":" + userId;
    }

    public void register(Long roomId, Long userId, WebSocketSession session) {
        String k = key(roomId, userId);
        WebSocketSession prev = byPair.put(k, session);
        pairBySessionId.put(session.getId(), k);

        if (prev != null && prev != session) {
            try {
                prev.close(CloseStatus.POLICY_VIOLATION);
            } catch (Exception ignore) {}
        }
    }

    public void unregister(WebSocketSession session) {
        String k = pairBySessionId.remove(session.getId());
        if (k != null) {
            WebSocketSession cur = byPair.get(k);
            if (cur == session) byPair.remove(k);
        }
    }

    public void closeIfPresent(Long roomId, Long userId) {
        String k = key(roomId, userId);
        WebSocketSession s = byPair.remove(k);
        if (s != null) {
            pairBySessionId.remove(s.getId());
            try {
                s.close(CloseStatus.NORMAL);
            } catch (Exception ignore) {}
        }
    }
}
