package com.example._thecore_back.hub.infrastructure.rabbitmq;

import com.example._thecore_back.hub.domain.dto.GpsLogDto;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GpsLogProducer {

    private final RabbitTemplate rabbitTemplate;

    @Async
    public void sendLogs(GpsLogDto gpsLogDto) {
        rabbitTemplate.convertAndSend("gps.data.exchange", "gps.data.*", gpsLogDto);
    }

}
