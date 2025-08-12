package com.example.whereshouldwego.messaging;

import com.example.whereshouldwego.dto.response.CustomUserDetails;
import com.example.whereshouldwego.dto.response.UserDto;
import com.example.whereshouldwego.jwt.JWTUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class StompAuthInterceptor implements ChannelInterceptor {

    private final JWTUtil jwtUtil;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) return message;

        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            String raw = accessor.getFirstNativeHeader("Authorization");
            String token = extractBearer(raw);
            if (token == null || jwtUtil.isExpired(token)) {
                throw new MessagingException("Unauthorized or invalid token");
            }

            String username = jwtUtil.getUsername(token);
            String role = jwtUtil.getRole(token);

            CustomUserDetails principal = new CustomUserDetails(UserDto.fromEntity(username, role, null, null, null));

            Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

            accessor.setUser(auth);
        }

        return message;
    }

    private static String extractBearer(String header) {
        return (header != null && header.startsWith("Bearer ")) ? header.substring(7) : null;
    }

}