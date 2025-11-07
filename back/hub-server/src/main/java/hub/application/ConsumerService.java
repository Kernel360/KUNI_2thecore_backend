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

@Service
@Slf4j
@RequiredArgsConstructor
public class ConsumerService {

    private final GpsLogService gpsLogService;
    private final CarRepository carRepository;
    private final LastPositionUpdator lastPositionUpdator;

    @Async("consumerTaskExecutor")
    @RabbitListener(queues = "gps.data.queue", errorHandler = "gpsConsumerErrorHandler")
    public void gpsConsumer(GpsLogDto gpsLogDto) {
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
                .toList();

        var entities = sortedGpsList.stream()
                .map(g -> new GpsLogEntity(gpsLogDto.getCarNumber(), g.getLatitude(), g.getLongitude(), g.getTimestamp()))
                .toList();

        gpsLogService.saveLog(entities);

        // DB에 60개 로그 저장이 끝난 후, 로그 남김
        log.info("청크 시작 전 carNumber : {}, logDto : {}", carEntity.getCarNumber(), sortedGpsList);

        lastPositionUpdator.replayGpsData(carEntity.getCarNumber(), sortedGpsList);



    }
}