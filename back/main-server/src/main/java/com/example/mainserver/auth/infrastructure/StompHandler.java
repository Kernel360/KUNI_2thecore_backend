package com.example.mainserver.auth.infrastructure;

import com.example.common.domain.auth.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // STOMP CONNECT 요청일 때만 JWT 인증 처리
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
            // 헤더에서 Authorization 토큰을 가져옴
            String jwtToken = accessor.getFirstNativeHeader("Authorization");
            log.debug("STOMP Authorization header: {}", jwtToken);

            if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
                String token = jwtToken.substring(7);
                // 토큰 유효성 검사 및 인증 정보 설정
                if (jwtTokenProvider.validateToken(token)) {
                    Authentication authentication = jwtTokenProvider.getAuthentication(token);
                    accessor.setUser(authentication);
                    log.debug("STOMP user set to: {}", authentication.getName());
                }
            }
        }
        return message;
    }
}
