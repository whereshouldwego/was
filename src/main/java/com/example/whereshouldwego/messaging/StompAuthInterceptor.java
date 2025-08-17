package com.example.whereshouldwego.messaging;

import com.example.whereshouldwego.dto.response.CustomUserDetails;
import com.example.whereshouldwego.dto.response.UserDto;
import com.example.whereshouldwego.jwt.JWTUtil;
import com.example.whereshouldwego.repository.postgres.RoomParticipantRepository;
import com.example.whereshouldwego.repository.postgres.UserRepository;
import lombok.Getter;
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

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.example.whereshouldwego.jwt.JWTUtil.extractBearer;
import static com.example.whereshouldwego.jwt.JWTUtil.extractRoomCode;
import static com.example.whereshouldwego.util.RoomCodeUtil.decode;

@Getter
@Component
@RequiredArgsConstructor
public class StompAuthInterceptor implements ChannelInterceptor {

    private final JWTUtil jwtUtil;

    // 세션별 가입한 roomId 캐시
    private final ConcurrentHashMap<String, Set<Long>> sessionRooms = new ConcurrentHashMap<>();

    private final UserRepository userRepository;
    private final RoomParticipantRepository roomParticipantRepository;


    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) return message;

        String sessionId = accessor.getSessionId();
        String destination = accessor.getDestination();
        StompCommand command = accessor.getCommand();

        if (StompCommand.CONNECT.equals(command)) {
            String raw = accessor.getFirstNativeHeader("Authorization");
            String token = extractBearer(raw);
            if (token == null || jwtUtil.isExpired(token)) {
                throw new MessagingException("Unauthorized or invalid token");
            }

            String username = jwtUtil.getUsername(token);
            String role = jwtUtil.getRole(token);

            CustomUserDetails principal = new CustomUserDetails(UserDto.of(username, role));

            Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

            accessor.setUser(auth);
        } else if (StompCommand.SUBSCRIBE.equals(command)) {
            String roomCode = extractRoomCode(destination);
            Long roomId = decode(roomCode);
            Long userId = currentUserId(accessor);

            if (!roomParticipantRepository.existsByRoomIdAndUserId(roomId, userId)) {
                throw new MessagingException("No permission for room " + roomCode);
            }
            sessionRooms.computeIfAbsent(sessionId, k -> ConcurrentHashMap.newKeySet()).add(roomId);
        } else if (StompCommand.SEND.equals(command)) {
            String roomCode = extractRoomCode(destination);
            Long roomId = decode(roomCode);

            if (!sessionRooms.getOrDefault(sessionId, Set.of()).contains(roomId)) {
                throw new MessagingException("No permission for room " + roomCode);
            }
        } else if (StompCommand.DISCONNECT.equals(command)) {
            sessionRooms.remove(sessionId);
        }

        return message;
    }

    private Long currentUserId(StompHeaderAccessor acc) {
        var user = acc.getUser();
        if (user == null) throw new MessagingException("Unauthenticated");

        var auth = (org.springframework.security.core.Authentication) user;
        var principal = (com.example.whereshouldwego.dto.response.CustomUserDetails) auth.getPrincipal();
        String username = principal.getUsername();

        return userRepository.findIdByUsername(username)
                .orElseThrow(() -> new MessagingException("User not found: " + username));
    }
}