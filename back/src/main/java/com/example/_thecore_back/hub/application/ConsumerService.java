package com.example._thecore_back.hub.application;


import com.example._thecore_back.hub.domain.GpsWebSocketHandler;
import com.example._thecore_back.hub.domain.dto.GpsLogDto;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConsumerService {

    private final GpsWebSocketHandler gpsWebSocketHandler;

    //rabbitmq Listen(큐에 메시지 진입 시 자동 실행)
    @RabbitListener(queues = "gps.data.queue")
    public void gpsConsumer(GpsLogDto gpsLogDto) {
        log.info("Message received from RabbitMQ: {}", gpsLogDto);
        try {
            gpsWebSocketHandler.sendGpsLogToClient(gpsLogDto);
        } catch (IOException e) {
            log.error("Failed to send message via WebSocket", e);
        }
    }
}
