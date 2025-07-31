package hub.application;


import hub.domain.GpsWebSocketHandler;
import hub.domain.dto.GpsLogDto;
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
    @RabbitListener(queues = "gps.data.queue", errorHandler = "gpsConsumerErrorHandler")
    public void gpsConsumer(GpsLogDto gpsLogDto) {
        log.info("Message received from RabbitMQ: {}", gpsLogDto);
        gpsWebSocketHandler.sendGpsLogToClient(gpsLogDto);
    }
}
