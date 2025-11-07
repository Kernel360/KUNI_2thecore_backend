package hub.application;

import com.example.common.domain.car.CarEntity;
import com.example.common.infrastructure.car.CarRepository;
import com.example.mainserver.car.exception.CarErrorCode;
import com.example.mainserver.car.exception.CarNotFoundException;
import hub.domain.dto.GpsLogDto;
import com.example.common.dto.LiveLocationDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class LastPositionUpdator {

    private final CarRepository carRepository;
    private final RabbitTemplate rabbitTemplate;
    private final CarLocationUpdateService carLocationUpdateService;
    private static final String LIVE_LOCATION_EXCHANGE_NAME = "live.location.exchange";

    @Async("replayTaskExecutor")
    public void replayGpsData(String carNumber, List<GpsLogDto.Gps> sortedGpsList) {

        log.info("Starting GPS data replay for car: {}", carNumber);

        CarEntity car = carRepository.findByCarNumber(carNumber).orElseThrow(() -> new CarNotFoundException(CarErrorCode.CAR_NOT_FOUND_BY_NUMBER, carNumber));

        // 60개의 GPS 데이터가 담긴 리스트를 처음부터 끝까지 하나씩 순회
        for (GpsLogDto.Gps gps : sortedGpsList) {

            try {

                // 현재 순회 중인 gps 데이터로 실시간 전송용 DTO 객체를 만듦
                LiveLocationDto liveLocationDto = new LiveLocationDto(carNumber, gps.getLatitude(), gps.getLongitude());

//                car.setLastLatitude(gps.getLatitude());
//                car.setLastLongitude(gps.getLongitude());
//
//                carRepository.save(car);

                carLocationUpdateService.updateLastLocation(carNumber, gps.getLatitude(), gps.getLongitude());

                // RabbitTemplate을 사용해서 Exchange에 메시지를 보냄
                rabbitTemplate.convertAndSend(LIVE_LOCATION_EXCHANGE_NAME, "", liveLocationDto);
                log.trace("Published live location for car {}: {}", carNumber, liveLocationDto);

                Thread.sleep(1000); // 1초 대기
            } catch (InterruptedException e) {
                // sleep 도중 스레드에 중단 요청이 오면 발생하는 예외 처리
                Thread.currentThread().interrupt();
                log.error("GPS replay interrupted for car: {}", carNumber, e);
                break; // 문제 발생하면 반복 중단
            }
        }
        log.info("Finished GPS data replay for car: {}", carNumber);
    }
}

