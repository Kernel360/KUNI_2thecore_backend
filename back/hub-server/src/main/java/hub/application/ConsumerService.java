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

    private final GpsLogRepository gpsLogRepository;
    private final CarRepository carRepository;
    private final LastPositionUpdator lastPositionUpdator;

    @Async
    @Transactional
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

        gpsLogRepository.saveAll(entities);


        log.info("청크 시작 전 carNumber : {}, logDto : {}", carEntity.getCarNumber(), sortedGpsList);
        lastPositionUpdator.scheduleEverySecond(carEntity.getCarNumber(), sortedGpsList);
//        //정렬된 리스트 차례로 저장
//        for (GpsLogDto.Gps gps : sortedGpsList) {
//            try {
//                GpsLogEntity gpsLogEntity = new GpsLogEntity(
//                        gpsLogDto.getCarNumber(),
//                        gps.getLatitude(),
//                        gps.getLongitude(),
//                        gps.getTimestamp()
//                );
//                gpsLogRepository.save(gpsLogEntity);
//
//                carEntity.setLastLatitude(gps.getLatitude());
//                carEntity.setLastLongitude(gps.getLongitude());
//                carRepository.save(carEntity);
//
//                log.info("Updated car {} position to lat: {}, lon: {}. Waiting 1 second.",
//                        gpsLogDto.getCarNumber(), gps.getLatitude(), gps.getLongitude());
//
//                Thread.sleep(1000);
//
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//                log.error("Thread interrupted during 1-second delay.", e);
//                break;
//            }
//        }
//        log.info("Finished processing all GPS logs for car: {}", gpsLogDto.getCarNumber());
//    }
    }
}