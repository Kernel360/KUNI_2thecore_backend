package hub.application;


import com.example.common.domain.car.CarEntity;
import com.example.common.infrastructure.car.CarRepository;
import hub.domain.GpsLogEntity;
import hub.domain.GpsLogRepository;
import hub.domain.dto.GpsLogDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConsumerService {

    private final GpsLogRepository gpsLogRepository;
    private final CarRepository carRepository;

    @RabbitListener(queues = "gps.data.queue", errorHandler = "gpsConsumerErrorHandler")
    public void gpsConsumer(GpsLogDto gpsLogDto) {
        log.info("gpsRabbitmq");
        processGpsLogAsync(gpsLogDto);
    }

    // 1초에 1번 갱신되도록
    @Async
    @Transactional
    public void processGpsLogAsync(GpsLogDto gpsLogDto) {
        log.info("Async processing started for car: {}", gpsLogDto.getCarNumber());

        Optional<CarEntity> optionalCar = carRepository.findByCarNumber(gpsLogDto.getCarNumber());
        if (optionalCar.isEmpty()) {
            log.warn("Car with carNumber {} not found.", gpsLogDto.getCarNumber());
            return;
        }
        CarEntity carEntity = optionalCar.get();

        //timestamp 기준 정렬
        List<GpsLogDto.Gps> sortedGpsList = gpsLogDto.getLogList().stream()
                .sorted(Comparator.comparing(GpsLogDto.Gps::getTimestamp))
                .collect(Collectors.toList());

        //정렬된 리스트 차례로 저장
        for (GpsLogDto.Gps gps : sortedGpsList) {
            try {
                GpsLogEntity gpsLogEntity = new GpsLogEntity(
                        gpsLogDto.getCarNumber(),
                        gps.getLatitude(),
                        gps.getLongitude(),
                        gps.getTimestamp()
                );
                gpsLogRepository.save(gpsLogEntity);

                carEntity.setLastLatitude(gps.getLatitude());
                carEntity.setLastLongitude(gps.getLongitude());
                carRepository.save(carEntity);

                log.info("Updated car {} position to lat: {}, lon: {}. Waiting 1 second.",
                        gpsLogDto.getCarNumber(), gps.getLatitude(), gps.getLongitude());

                Thread.sleep(1000);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.error("Thread interrupted during 1-second delay.", e);
                break;
            }
        }
        log.info("Finished processing all GPS logs for car: {}", gpsLogDto.getCarNumber());
    }

    //rabbitmq 거치지 않은 메소드
    public void gpsConsumerDirect(GpsLogDto gpsLogDto) {
        log.info("gpsConsumerDirect");
        processGpsLogAsync(gpsLogDto);
    }

}
