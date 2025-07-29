package com.example._thecore_back.hub.config;

import com.example._thecore_back.hub.domain.GpsWebSocketHandler;
import com.example._thecore_back.hub.interceptor.JwtHandshakeInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {

    private final GpsWebSocketHandler gpsWebSocketHandler;
    private final JwtHandshakeInterceptor jwtHandshakeInterceptor;

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(gpsWebSocketHandler, "/map/running")
                //CORS 설정
                .setAllowedOrigins("*")
                //handshake 시 jwt에서 loginId 추출을 위한 interceptor
                .addInterceptors(jwtHandshakeInterceptor);
    }
}
