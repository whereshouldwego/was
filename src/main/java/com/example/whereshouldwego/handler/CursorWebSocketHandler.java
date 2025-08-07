package com.example.whereshouldwego.handler;

import com.example.whereshouldwego.domain.CursorMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.concurrent.*;

@Component
public class CursorWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final Map<String, Set<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();

    private final Map<String, String> sessionRoomMap = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String uri = Objects.requireNonNull(session.getUri()).toString();
        String roomCode = extractRoomCode(uri);

        if (roomCode == null) {
            session.close(CloseStatus.BAD_DATA);
            return;
        }

        roomSessions
                .computeIfAbsent(roomCode, k -> ConcurrentHashMap.newKeySet())
                .add(session);

        sessionRoomMap.put(session.getId(), roomCode);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        CursorMessage cursor = objectMapper.readValue(message.getPayload(), CursorMessage.class);
        String roomCode = cursor.getRoomCode();

        String broadcast = objectMapper.writeValueAsString(cursor);
        Set<WebSocketSession> sessions = roomSessions.getOrDefault(roomCode, Collections.emptySet());

        sessions.forEach(s -> {
            if (s.isOpen()) {
                CompletableFuture.runAsync(() -> {
                    try {
                        s.sendMessage(new TextMessage(broadcast));
                    } catch (Exception e) {

                    }
                });
            }
        });
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String sessionId = session.getId();
        String roomCode = sessionRoomMap.remove(sessionId);

        if (roomCode != null) {
            Set<WebSocketSession> sessions = roomSessions.get(roomCode);
            if (sessions != null) {
                sessions.remove(session);
                if (sessions.isEmpty()) {
                    roomSessions.remove(roomCode);
                }
            }
        }
    }

    private String extractRoomCode(String uri) {
        return UriComponentsBuilder.fromUriString(uri)
                .build()
                .getQueryParams()
                .getFirst("roomCode");
    }
}
