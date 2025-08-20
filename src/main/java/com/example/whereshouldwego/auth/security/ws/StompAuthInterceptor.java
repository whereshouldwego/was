package com.example.whereshouldwego.auth.security.ws;

import com.example.whereshouldwego.features.user.dto.response.CustomUserDetails;
import com.example.whereshouldwego.features.user.dto.response.UserDto;
import com.example.whereshouldwego.auth.security.jwt.JwtUtil;
import com.example.whereshouldwego.features.room.repository.RoomParticipantRepository;
import com.example.whereshouldwego.features.user.repository.UserRepository;
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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.example.whereshouldwego.auth.security.jwt.JwtUtil.extractBearer;
import static com.example.whereshouldwego.auth.security.jwt.JwtUtil.extractRoomCode;
import static com.example.whereshouldwego.common.util.RoomCodeUtil.decode;

@Getter
@Component
@RequiredArgsConstructor
public class StompAuthInterceptor implements ChannelInterceptor {

    private final JwtUtil jwtUtil;

    // sessionId -> roomId
    private final ConcurrentHashMap<String, Long> sessionCurrentRoom = new ConcurrentHashMap<>();

    // sessionId -> (subscriptionId -> roomId)
    private final ConcurrentHashMap<String, Map<String, Long>> sessionSubRoom = new ConcurrentHashMap<>();

    // sessionId -> userId
    private final ConcurrentHashMap<String, Long> sessionUserId = new ConcurrentHashMap<>();

    private final UserRepository userRepository;
    private final RoomParticipantRepository roomParticipantRepository;


    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null) return message;

        String sessionId = accessor.getSessionId();
        StompCommand command = accessor.getCommand();

        if (StompCommand.CONNECT.equals(command)) {
            String raw = accessor.getFirstNativeHeader("Authorization");
            String token = extractBearer(raw);
            if (token == null || jwtUtil.isExpired(token)) {
                throw new MessagingException("Unauthorized or invalid token");
            }

            Long userId = jwtUtil.getUserId(token);
            String role = jwtUtil.getRole(token);

            CustomUserDetails principal = new CustomUserDetails(UserDto.of(userId, role));
            Authentication auth = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());

            accessor.setUser(auth);
            sessionUserId.put(sessionId, userId);
        } else if (StompCommand.SUBSCRIBE.equals(command)) {
            String destination = accessor.getDestination();
            if (destination == null) throw new MessagingException("SUBSCRIBE requires destination");

            String roomCode = extractRoomCode(destination);
            Long roomId = decode(roomCode);

            // 멤버십 검증
            Authentication authentication = (Authentication) accessor.getUser();
            CustomUserDetails principal = (CustomUserDetails) authentication.getPrincipal();
            Long userId = principal.getUserId();
            if (!roomParticipantRepository.existsByRoomIdAndUserId(roomId, userId)) {
                throw new MessagingException("No permission for room " + roomCode);
            }

            // 한 소켓 = 한 방 강제
            Long current = sessionCurrentRoom.putIfAbsent(sessionId, roomId);
            if (current != null && !current.equals(roomId)) {
                throw new MessagingException("Already joined another room; disconnect and reconnect to switch.");
            }

            // subscriptionId 추적
            String subscriptionId = accessor.getSubscriptionId();
            if (subscriptionId != null) {
                sessionSubRoom
                        .computeIfAbsent(sessionId, k -> new ConcurrentHashMap<>())
                        .put(subscriptionId, roomId);
            }
        } else if (StompCommand.SEND.equals(command)) {
            String destination = accessor.getDestination();
            if (destination == null) throw new MessagingException("SEND requires destination");

            String roomCode = extractRoomCode(destination);
            Long roomId = decode(roomCode);

            Long current = sessionCurrentRoom.get(sessionId);
            if (current == null || !current.equals(roomId)) {
                throw new MessagingException("No permission for room " + roomCode);
            }
        } else if (StompCommand.UNSUBSCRIBE.equals(command)) {
            String subscriptionId = accessor.getSubscriptionId();
            if (subscriptionId != null) {
                Map<String, Long> map = sessionSubRoom.get(sessionId);
                if (map != null) map.remove(subscriptionId);
            }
        } else if (StompCommand.DISCONNECT.equals(command)) {
            sessionSubRoom.remove(sessionId);
            sessionCurrentRoom.remove(sessionId);
            sessionUserId.remove(sessionId);
        }

        return message;
    }
}