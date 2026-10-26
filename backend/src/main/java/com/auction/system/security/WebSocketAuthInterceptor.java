package com.auction.system.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {
    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;

    public WebSocketAuthInterceptor(
            JwtTokenProvider tokenProvider,
            UserDetailsService userDetailsService) {
        this.tokenProvider = tokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        if (accessor == null || accessor.getCommand() != StompCommand.CONNECT) {
            return message;
        }

        String authorization = accessor.getFirstNativeHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            throw new AccessDeniedException("Cần đăng nhập để kết nối phiên đấu giá.");
        }

        try {
            Claims claims = tokenProvider.parse(authorization.substring(7));
            if (!"access".equals(claims.get("type", String.class))) {
                throw new AccessDeniedException("Access token không hợp lệ.");
            }
            UserDetails user = userDetailsService.loadUserByUsername(claims.getSubject());
            if (!user.isEnabled()) {
                throw new AccessDeniedException("Tài khoản hiện không hoạt động.");
            }
            accessor.setUser(new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities()));
            return message;
        } catch (JwtException | IllegalArgumentException exception) {
            throw new AccessDeniedException("Access token không hợp lệ.", exception);
        }
    }
}
