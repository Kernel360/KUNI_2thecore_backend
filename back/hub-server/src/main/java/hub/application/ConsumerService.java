package hub.application;


import hub.domain.GpsLogEntity;
import hub.domain.GpsLogRepository;
import hub.domain.dto.GpsLogDto;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConsumerService {

    private final GpsLogRepository gpsLogRepository;

    @RabbitListener(queues = "gps.data.queue", errorHandler = "gpsConsumerErrorHandler")
    public void gpsConsumer(GpsLogDto gpsLogDto) {

        log.info("Message received from RabbitMQ: {}", gpsLogDto);

        // 주기(60초,120초,180초) 단위 DB 저장
        List<GpsLogEntity> gpsLogEntities = gpsLogDto.getLogList().stream()
            .map(gps -> new GpsLogEntity(
                gpsLogDto.getCarNumber(),
                gps.getLatitude(),
                gps.getLongitude(),
                gps.getTimestamp()))
            .collect(Collectors.toList());

        gpsLogRepository.saveAll(gpsLogEntities);
    }
}
