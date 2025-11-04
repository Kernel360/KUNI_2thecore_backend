package com.example.mainserver.config;

import com.example.mainserver.auth.infrastructure.StompHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {
    private final StompHandler stompHandler;

    // 클라이언트가 웹소켓 서버에 연결하는데 사용할 엔드포인트 등록
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // STOMP 접속 주소: "/ws
        registry.addEndpoint("/ws").setAllowedOriginPatterns("*");
    }

    // 메세지 브로커 구성
    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // "/location"로 시작하는 주소를 구독하는 클라이언트에게 메세지를 전달하는 브로커
        registry.enableSimpleBroker("/location");

        // 클라이언트가 서버로 메세지를 보낼 때 사용하는 주소의 prefix
        // 현재는 사용 X
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(stompHandler);
    }
}