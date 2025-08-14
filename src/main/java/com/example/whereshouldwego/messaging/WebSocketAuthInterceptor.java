package com.example.whereshouldwego.messaging;

import com.example.whereshouldwego.dto.response.CustomUserDetails;
import com.example.whereshouldwego.dto.response.UserDto;
import com.example.whereshouldwego.jwt.JWTUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;

@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements HandshakeInterceptor {

    private final JWTUtil jwtUtil;

    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Map<String, Object> attributes) throws Exception {

        if (request instanceof ServletServerHttpRequest servletRequest) {
            HttpServletRequest httpRequest = servletRequest.getServletRequest();

            String raw = httpRequest.getParameter("token");
            if (raw == null) {
                raw = httpRequest.getHeader("Authorization");
            }

            String token =extractBearer(raw);

            if (token == null || jwtUtil.isExpired(token)) {
                if (response instanceof HttpServletResponse servletResponse) {
                    servletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized or invalid token");
                }
                return false;
            }

            String username = jwtUtil.getUsername(token);
            String role = jwtUtil.getRole(token);

            CustomUserDetails principal = new CustomUserDetails(
                    UserDto.fromEntity(username, role)
            );
            Authentication auth = new UsernamePasswordAuthenticationToken(
                    principal, null, principal.getAuthorities()
            );

            attributes.put("user", auth);
        }
        return true;
    }


    @Override
    public void afterHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response,
            WebSocketHandler wsHandler,
            Exception exception) {}

    private static String extractBearer(String header) {
        return (header != null && header.startsWith("Bearer ")) ? header.substring(7) : null;
    }
}
