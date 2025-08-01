<<<<<<<< HEAD:back/src/main/java/com/example/_thecore_back/collector/config/RabbitMqConfig.java
package com.example._thecore_back.collector.config;
========
package hub.config;
>>>>>>>> Kernel360/mq:back/hub-server/src/main/java/hub/config/RabbitMqConfig.java

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    private static final String EXCHANGE_NAME = "gps.data.exchange";
    private static final String QUEUE_NAME = "gps.data.queue";
    private static final String ROUTING_KEY = "gps.data.*";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue queue() {
        return new Queue(QUEUE_NAME);
    }

    @Bean
    public Binding binding(Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }

    // 객체 -> json(byte) / (byte)json -> 객체 자동 변환
    @Bean
    public MessageConverter messageConverter(ObjectMapper objectMapper){
        return new Jackson2JsonMessageConverter(objectMapper);
    }
}
