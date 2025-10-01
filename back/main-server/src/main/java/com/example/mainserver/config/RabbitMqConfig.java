package com.example.mainserver.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {
    private static final String LIVE_LOCATION_EXCHANGE = "";
    private static final String LIVE_LOCATION_QUEUE = "";

    @Bean
    public FanoutExchange liveLocationExchange() {
        // Hub서버와 동일한 생성한 Exchange 선언
        return new FanoutExchange(LIVE_LOCATION_EXCHANGE);
    }

    @Bean
    public Queue liveLocationQueue() {
        // Exchange로부터 수신할 큐 생성
        // 서버 재시작될 때마다 새로운 큐 생성, 연결이 끊어지면 자동 삭제
        return new Queue(LIVE_LOCATION_QUEUE, false, false, true);
    }

    @Bean
    public Binding binding(Queue liveLocationQueue, FanoutExchange liveLocationExchange) {
        // Exchange와 Queue 바인딩
        return BindingBuilder.bind(liveLocationQueue).to(liveLocationExchange);
    }
}
