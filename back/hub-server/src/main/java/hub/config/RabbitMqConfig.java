package hub.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMqConfig {

    private static final String EXCHANGE_NAME = "gps.data.exchange";
    private static final String LIVE_LOCATION_EXCHANGE_NAME = "live.location.exchange"; // 웹소켓 사용
    private static final String QUEUE_NAME = "gps.data.queue";
    private static final String ROUTING_KEY = "gps.data.*";

    @Bean
    public TopicExchange exchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    // 웹소켓 실시간 중계를 위한 Fanout Exchange
    @Bean
    public org.springframework.amqp.core.FanoutExchange liveLocationExchange() {
        return new org.springframework.amqp.core.FanoutExchange(LIVE_LOCATION_EXCHANGE_NAME);
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
