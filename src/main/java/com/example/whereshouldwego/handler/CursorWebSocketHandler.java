package com.example.whereshouldwego.handler;

import com.example.whereshouldwego.domain.Cursor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.concurrent.*;

@Component
public class CursorWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final TaskExecutor taskExecutor;

    private final Map<String, Set<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();
    private final Map<String, String> sessionRoomMap = new ConcurrentHashMap<>();

    private static final String ATTR_ROOM_CODE = "roomCode";

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String roomCode = resolveRoomCode(session.getUri());
        if (roomCode == null || roomCode.isBlank()) {
            session.close(CloseStatus.BAD_DATA);
            return;
        }

        session.getAttributes().put(ATTR_ROOM_CODE, roomCode);

        roomSessions
                .computeIfAbsent(roomCode, k -> new CopyOnWriteArraySet<>())
                .add(session);

        sessionRoomMap.put(session.getId(), roomCode);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        final String roomCode = (String) session.getAttributes().getOrDefault(ATTR_ROOM_CODE, sessionRoomMap.get(session.getId()));

        if (roomCode == null) { return; }

        Cursor cursor;
        try {
            cursor = objectMapper.readValue(message.getPayload(), Cursor.class);
        } catch (Exception e) {
            return;
        }

        if (cursor.getRoomCode() == null || !roomCode.equals(cursor.getRoomCode())) {
            return;
        }

        final String broadcast;
        try {
            broadcast = objectMapper.writeValueAsString(cursor);
        } catch (Exception e) {
            return;
        }

        Set<WebSocketSession> sessions = roomSessions.getOrDefault(roomCode, Collections.emptySet());
        for (WebSocketSession s : sessions) {
            // 송신자 제외
            if (s.getId().equals(session.getId())) continue;

            if (!s.isOpen()) continue;

            // 전송은 세션 단위 동기화 + 풀에서 비동기
            taskExecutor.execute(() -> {
                try {
                    synchronized (s) {
                        s.sendMessage(new TextMessage(broadcast));
                    }
                } catch (Exception sendEx) {
                    safeRemoveSession(s);
                }
            });
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        safeRemoveSession(session);
        if (session.isOpen()) {
            session.close(CloseStatus.SERVER_ERROR);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        safeRemoveSession(session);
    }

    private void safeRemoveSession(WebSocketSession session) {
        String roomCode = (String) session.getAttributes().getOrDefault(ATTR_ROOM_CODE, sessionRoomMap.remove(session.getId()));
        if (roomCode == null) return;

        Set<WebSocketSession> set = roomSessions.get(roomCode);
        if (set != null) {
            set.remove(session);
            if (set.isEmpty()) {
                roomSessions.remove(roomCode);
            }
        }
    }

    private String resolveRoomCode(URI uri) {
        if (uri == null) return null;
        return UriComponentsBuilder.fromUri(uri).build().getQueryParams().getFirst("roomCode");
    }
}
