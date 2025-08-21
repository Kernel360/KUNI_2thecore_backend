package hub.infrastructure;

import com.example.common.domain.car.CarEntity;
import com.example.common.infrastructure.car.CarRepository;
import hub.domain.CarPositionWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.RestClientException;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CarPostionWriterImpl implements CarPositionWriter {

    private final CarRepository carRepository;
    
    @Autowired(required = false)
    private RestTemplate restTemplate;


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
        String oldLat = car.getLastLatitude();
        String oldLon = car.getLastLongitude();

        car.setLastLatitude(lat);
        car.setLastLongitude(lon);
        carRepository.save(car);
        log.info("car {} lastPostion -> {}, {}", car.getCarNumber(), car.getLastLatitude(), car.getLastLongitude());
        
        // 메인 서버에 실시간 드라이브 로그 업데이트 요청
        updateDriveLogIfExists(car.getId(), lat, lon, oldLat, oldLon);
    }
    
    private void updateDriveLogIfExists(int carId, String newLat, String newLon, String oldLat, String oldLon) {
        if (restTemplate == null) {
            log.debug("RestTemplate not available, skipping drive log update");
            return;
        }
        
        if (oldLat == null || oldLon == null) {
            log.debug("No previous position for car {}, skipping drive log update", carId);
            return;
        }
        
        try {
            String url = "http://localhost:8080/api/drive-logs/update-location";
            
            // 요청 DTO 생성 (간단한 Map 사용)
            var request = new java.util.HashMap<String, Object>();
            request.put("carId", Long.valueOf(carId));
            request.put("newLatitude", newLat);
            request.put("newLongitude", newLon);
            
            restTemplate.postForObject(url, request, String.class);
            log.info("Updated drive log for car {} with new position {}, {}", carId, newLat, newLon);
            
        } catch (RestClientException e) {
            log.warn("Failed to update drive log for car {}: {}", carId, e.getMessage());
        }
    }
}
