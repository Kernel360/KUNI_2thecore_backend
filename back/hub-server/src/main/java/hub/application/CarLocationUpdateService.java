package hub.application;

import com.example.common.domain.car.CarEntity;
import com.example.common.infrastructure.car.CarRepository;
import com.example.mainserver.car.exception.CarErrorCode;
import com.example.mainserver.car.exception.CarNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CarLocationUpdateService {

    private final CarRepository carRepository;

    @Transactional
    @Retryable(
            value = { OptimisticLockingFailureException.class, ObjectOptimisticLockingFailureException.class },
            maxAttempts = 3, backoff = @Backoff(delay = 50)
    )
    public void updateLastLocation(String carNumber, String latitude, String longitude) {
        try {
            CarEntity car = carRepository.findByCarNumber(carNumber)
                    .orElseThrow(() -> new CarNotFoundException(CarErrorCode.CAR_NOT_FOUND_BY_NUMBER, carNumber));

            car.setLastLatitude(latitude);
            car.setLastLongitude(longitude);

            carRepository.save(car);

        } catch(OptimisticLockingFailureException e) {
            log.warn("위치 optimistic lock 충돌 : {}", carNumber);
            throw e;
        }
    }

}
