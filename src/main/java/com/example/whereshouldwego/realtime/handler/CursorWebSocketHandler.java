package com.example.whereshouldwego.realtime.handler;

import com.example.whereshouldwego.realtime.session.RawWebSocketSessionRegistry;
import com.example.whereshouldwego.realtime.dto.request.CursorRequest;
import com.example.whereshouldwego.realtime.dto.response.CursorResponse;
import com.example.whereshouldwego.features.room.repository.RoomParticipantRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.task.TaskExecutor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.example.whereshouldwego.auth.security.ws.RawWebSocketAuthInterceptor.*;


@Component
@RequiredArgsConstructor
public class CursorWebSocketHandler extends TextWebSocketHandler {

    // roomCode -> sessions
    private final ConcurrentHashMap<String, Set<WebSocketSession>> roomSessions = new ConcurrentHashMap<>();
    // sessionId -> roomCode
    private final ConcurrentHashMap<String, String> sessionRoomMap = new ConcurrentHashMap<>();

    private final ObjectMapper objectMapper;
    private final @Qualifier("websocketTaskExecutor") TaskExecutor taskExecutor;
    private final RawWebSocketSessionRegistry rawWebSocketSessionRegistry;
    private final RoomParticipantRepository roomParticipantRepository;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String roomCode = (String) session.getAttributes().get(ATTR_ROOM_CODE);
        if (roomCode == null || roomCode.isBlank()) {
            session.close(CloseStatus.BAD_DATA);
            return;
        }

        Long roomId = (Long) session.getAttributes().get(ATTR_ROOM_ID);
        Long userId = (Long) session.getAttributes().get(ATTR_USER_ID);
        if (roomId != null && userId != null) {
            rawWebSocketSessionRegistry.register(roomId, userId, session);
        }

        roomSessions.computeIfAbsent(roomCode, k -> ConcurrentHashMap.newKeySet()).add(session);
        sessionRoomMap.put(session.getId(), roomCode);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        rawWebSocketSessionRegistry.unregister(session);
        safeRemoveSession(session);
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String roomCode = (String) session.getAttributes().get(ATTR_ROOM_CODE);
        Authentication auth = (Authentication) session.getAttributes().get(ATTR_AUTH);

        if (roomCode == null || auth == null || auth.getPrincipal() == null) return;

        CursorRequest cursorRequest = objectMapper.readValue(message.getPayload(), CursorRequest.class);
        CursorResponse cursorResponse = CursorResponse.from(cursorRequest);

        final String payload = objectMapper.writeValueAsString(cursorResponse);

        Set<WebSocketSession> sessions = roomSessions.getOrDefault(roomCode, Collections.emptySet());
        for (WebSocketSession s : sessions) {
            if (s.getId().equals(session.getId())) continue; // 송신자 제외
            if (!s.isOpen()) continue;

            taskExecutor.execute(() -> {
                try {
                    synchronized (s) {
                        s.sendMessage(new TextMessage(payload));
                    }
                } catch (Exception ignore) {
                    rawWebSocketSessionRegistry.unregister(s);
                    safeRemoveSession(s);
                }
            });
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        rawWebSocketSessionRegistry.unregister(session);
        safeRemoveSession(session);
        if (session.isOpen()) session.close(CloseStatus.SERVER_ERROR);
    }

    private void safeRemoveSession(WebSocketSession session) {
        String roomCode = (String) session.getAttributes().get(ATTR_ROOM_CODE);
        if (roomCode == null) {
            roomCode = sessionRoomMap.remove(session.getId());
        } else {
            sessionRoomMap.remove(session.getId());
        }
        if (roomCode == null) return;

        Set<WebSocketSession> set = roomSessions.get(roomCode);
        if (set != null) {
            set.remove(session);
            if (set.isEmpty()) roomSessions.remove(roomCode);
        }
    }
}
