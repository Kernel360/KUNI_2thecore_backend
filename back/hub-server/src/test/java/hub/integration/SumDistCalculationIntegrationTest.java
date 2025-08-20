package hub.integration;

import com.example.common.domain.car.CarEntity;
import com.example.common.infrastructure.car.CarRepository;
import hub.infrastructure.CarPostionWriterImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:h2:mem:testdb",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Transactional
class SumDistCalculationIntegrationTest {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CarPostionWriterImpl carPositionWriter;

    @Test
    void testSumDistCalculation() {
        // Given: 테스트 차량 생성
        CarEntity car = CarEntity.builder()
                .carNumber("테스트123")
                .brand("현대")
                .model("소나타")
                .carYear(2023)
                .sumDist(0.0)
                .build();
        carRepository.save(car);

        // When: 첫 번째 위치 설정 (서울역)
        carPositionWriter.updateOnce("테스트123", "37.5665", "126.9780");
        
        // Then: 첫 번째 위치는 거리 계산 안됨
        CarEntity updatedCar = carRepository.findByCarNumber("테스트123").orElseThrow();
        assertEquals(0.0, updatedCar.getSumDist(), 0.001);
        assertEquals("37.5665", updatedCar.getLastLatitude());
        assertEquals("126.9780", updatedCar.getLastLongitude());

        // When: 두 번째 위치로 이동 (강남역 - 약 8.8km)
        carPositionWriter.updateOnce("테스트123", "37.4979", "127.0276");
        
        // Then: 거리가 계산되어 sumDist에 추가됨
        updatedCar = carRepository.findByCarNumber("테스트123").orElseThrow();
        assertTrue(updatedCar.getSumDist() > 8.0 && updatedCar.getSumDist() < 10.0,
                  "sumDist should be around 8.8km, but was: " + updatedCar.getSumDist());
        assertEquals("37.4979", updatedCar.getLastLatitude());
        assertEquals("127.0276", updatedCar.getLastLongitude());

        // When: 세 번째 위치로 이동 (추가 거리)
        double previousSumDist = updatedCar.getSumDist();
        carPositionWriter.updateOnce("테스트123", "37.5044", "127.0477");
        
        // Then: 이전 거리에 새 거리가 누적됨
        updatedCar = carRepository.findByCarNumber("테스트123").orElseThrow();
        assertTrue(updatedCar.getSumDist() > previousSumDist,
                  "sumDist should increase from: " + previousSumDist + " to: " + updatedCar.getSumDist());
        
        System.out.println("Final sumDist: " + updatedCar.getSumDist() + " km");
    }
}