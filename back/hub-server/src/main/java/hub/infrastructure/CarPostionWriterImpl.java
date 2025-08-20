package hub.infrastructure;

import com.example.common.domain.car.CarEntity;
import com.example.common.infrastructure.car.CarRepository;
import hub.domain.CarPositionWriter;
import hub.util.DistanceCalculator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CarPostionWriterImpl implements CarPositionWriter {

    private final CarRepository carRepository;


    @Override
    @Transactional
    @Async
    public void updateOnce(String carNumber, String lat, String lon) {

        Optional<CarEntity> optionalCar = carRepository.findByCarNumber(carNumber);

        if (optionalCar.isEmpty()) {
            log.warn("Car with carNumber {} not found.", carNumber);
            return;
        }

        var car = optionalCar.get();

        // 이전 위치가 있을 때만 거리 계산
        if (car.getLastLatitude() != null && car.getLastLongitude() != null 
            && !car.getLastLatitude().isEmpty() && !car.getLastLongitude().isEmpty()) {
            
            double distance = DistanceCalculator.calculateDistance(
                car.getLastLatitude(), car.getLastLongitude(),  // 이전 위치
                lat, lon  // 새 위치
            );
            
            // 유효한 거리인 경우에만 sumDist에 추가
            if (DistanceCalculator.isValidDistance(distance)) {
                car.setSumDist(car.getSumDist() + distance);
                log.info("car {} moved {} km, total sumDist: {} km", carNumber, distance, car.getSumDist());
            } else {
                log.warn("car {} invalid distance {} km, skipping sumDist update", carNumber, distance);
            }
        } else {
            // 첫 번째 위치일 때는 로그만
            log.info("car {} first position set: {}, {}", carNumber, lat, lon);
        }

        // 새 위치 저장
        car.setLastLatitude(lat);
        car.setLastLongitude(lon);
        carRepository.save(car);
        log.info("car {} position updated -> {}, {}", car.getCarNumber(), car.getLastLatitude(), car.getLastLongitude());
    }
}
