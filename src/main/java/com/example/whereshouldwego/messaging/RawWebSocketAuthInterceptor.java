package com.example.whereshouldwego.messaging;

import com.example.whereshouldwego.dto.response.CustomUserDetails;
import com.example.whereshouldwego.dto.response.UserDto;
import com.example.whereshouldwego.jwt.JWTUtil;
import com.example.whereshouldwego.repository.postgres.RoomParticipantRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

import static com.example.whereshouldwego.jwt.JWTUtil.extractBearer;
import static com.example.whereshouldwego.util.RoomCodeUtil.decode;

@Component
@RequiredArgsConstructor
public class RawWebSocketAuthInterceptor implements HandshakeInterceptor {

    public static final String ATTR_ROOM_CODE = "roomCode";
    public static final String ATTR_ROOM_ID = "roomId";
    public static final String ATTR_USER_ID = "userId";
    public static final String ATTR_AUTH = "auth";

    private final JWTUtil jwtUtil;
    private final RoomParticipantRepository roomParticipantRepository;

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes
    ) throws Exception {

        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest httpRequest = servletRequest.getServletRequest();

            String roomCode = httpRequest.getParameter("roomCode");
            if (roomCode == null || roomCode.isBlank()) {
                if (response instanceof ServletServerHttpResponse ssr) {
                    ssr.getServletResponse().sendError(HttpServletResponse.SC_BAD_REQUEST, "roomCode is required");
                }
                return false;
            }

            Long roomId = decode(roomCode);

            String token = extractBearer(httpRequest.getParameter("token"));
            if (token == null || jwtUtil.isExpired(token)) {
                if (response instanceof ServletServerHttpResponse ssr) {
                    ssr.getServletResponse().sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized or invalid token");
                }
                return false;
            }

            Long userId = jwtUtil.getUserId(token);
            String role = jwtUtil.getRole(token);

            if (userId == null) {
                if (response instanceof ServletServerHttpResponse ssr) {
                    ssr.getServletResponse().sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not found");
                }
                return false;
            }

            // 멤버십 검증
            if (!roomParticipantRepository.existsByRoomIdAndUserId(roomId, userId)) {
                if (response instanceof ServletServerHttpResponse ssr) {
                    ssr.getServletResponse().sendError(HttpServletResponse.SC_FORBIDDEN, "No permission for room");
                }
                return false;
            }

            CustomUserDetails principal = new CustomUserDetails(UserDto.of(userId, role));
            Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

            attributes.put(ATTR_ROOM_CODE, roomCode);
            attributes.put(ATTR_ROOM_ID, roomId);
            attributes.put(ATTR_USER_ID, userId);
            attributes.put(ATTR_AUTH, auth);
        }
        return true;
    }

    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception
    ) {
    }
}
