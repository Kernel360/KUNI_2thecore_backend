package com.example.mainserver.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    private static final String LIVE_LOCATION_EXCHANGE = "live.location.exchange";
    private static final String LIVE_LOCATION_QUEUE = "live.location.queue.main-server";

    // 주행기록 생성 및 종료시 rabbitmq로 전송
    private static final String DRIVE_LOG_EXCHANGE = "drive.log.exchange";
    private static final String DRIVE_LOG_QUEUE = "drive.log.queue";
    private static final String DRIVE_LOG_ROUTING_KEY = "drive.log.*";

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
    public Binding liveLocationBinding(Queue liveLocationQueue, FanoutExchange liveLocationExchange) {
        // Exchange와 Queue 바인딩
        return BindingBuilder.bind(liveLocationQueue).to(liveLocationExchange);
    }

    // main server -> hub server 설정
    private static final String EXCHANGE_NAME = "gps.data.exchange";
    private static final String QUEUE_NAME = "gps.data.queue";
    private static final String ROUTING_KEY = "gps.data.*";

    @Bean
    public TopicExchange gpsLogExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue gpsLogQueue() {
        return new Queue(QUEUE_NAME);
    }

    @Bean
    public Binding gpsLogBinding(Queue gpsLogQueue, TopicExchange gpsLogExchange) {
        return BindingBuilder.bind(gpsLogQueue).to(gpsLogExchange).with(ROUTING_KEY);
    }

    // 객체 -> json(byte) / (byte)json -> 객체 자동 변환
    @Bean
    public MessageConverter messageConverter(ObjectMapper objectMapper){
        return new Jackson2JsonMessageConverter(objectMapper);
    }


    // 주행 기록 생성 및 저장을 위한 데이터 rabbitmq
    @Bean
    public Queue driveLogEventQueue(){
        return new Queue(DRIVE_LOG_QUEUE, true);
    }

    @Bean
    public TopicExchange driveLogEventExchange() {
        return new TopicExchange(DRIVE_LOG_EXCHANGE);
    }

    @Bean
    public Binding driveLogEventBinding(TopicExchange driveLogEventExchange, Queue driveLogEventQueue) {
        return BindingBuilder.bind(driveLogEventQueue).to(driveLogEventExchange).with(DRIVE_LOG_ROUTING_KEY);
    }
}
