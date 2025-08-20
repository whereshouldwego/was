package com.example.whereshouldwego.common.logging;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import static com.example.whereshouldwego.auth.security.jwt.JwtUtil.extractRoomCode;

@Slf4j
@Component
public class LoggingMdcInterceptor implements ChannelInterceptor {

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null) {
            String destination = accessor.getDestination();

            if (destination != null) {
                String room = extractRoomCode(destination);
                String user = accessor.getUser() != null ? accessor.getUser().getName() : "anonymous";
                log.debug("STOMP cmd={} dest={} room={} user={}", accessor.getCommand(), destination, room, user);
            }
        }
        return message;
    }

    @Override
    public void afterSendCompletion(Message<?> message, MessageChannel channel, boolean sent, Exception ex) {
        MDC.clear();
    }
}
