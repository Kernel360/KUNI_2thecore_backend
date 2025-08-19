package com.example.mainserver.collector.infrastructure.rabbitmq;

import com.example.mainserver.collector.domain.dto.GpsLogDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GpsLogProducer {

    private final RabbitTemplate rabbitTemplate;

    @Async
    public void sendLogs(GpsLogDto gpsLogDto) {
        rabbitTemplate.convertAndSend("gps.data.exchange", "gps.data.*", gpsLogDto);
    }

}
