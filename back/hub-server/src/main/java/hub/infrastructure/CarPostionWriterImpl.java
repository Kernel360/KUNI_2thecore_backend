package hub.infrastructure;

import com.example.common.domain.car.CarEntity;
import com.example.common.infrastructure.car.CarRepository;
import hub.domain.CarPositionWriter;
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

        car.setLastLatitude(lat);
        car.setLastLongitude(lon);
        carRepository.save(car);
        log.info("car {} lastPostion -> {}, {}", car.getCarNumber(), car.getLastLatitude(), car.getLastLongitude());
    }
}
