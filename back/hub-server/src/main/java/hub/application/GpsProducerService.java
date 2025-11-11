package hub.application;

import hub.domain.dto.GpsLogDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class GpsProducerService {

    private final RabbitTemplate rabbitTemplate;

    private static final String EXCHANGE_NAME = "gps.data.exchange";
    private static final String ROUTING_KEY = "gps.data.location";

    public void sendGpsData(GpsLogDto gpsLogDto) {
        log.info("🚀 Publishing GPS data to RabbitMQ: carNumber={}, logs={}",
                gpsLogDto.getCarNumber(), gpsLogDto.getLogList().size());
        rabbitTemplate.convertAndSend(EXCHANGE_NAME, ROUTING_KEY, gpsLogDto);
    }
}
