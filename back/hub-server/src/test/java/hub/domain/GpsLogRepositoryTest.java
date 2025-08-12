package hub.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest
class GpsLogRepositoryTest {

    @Autowired
    private GpsLogRepository gpsLogRepository;

    @Test
    @DisplayName("GpsLog 저장 및 조회 테스트")
    void saveAndFindTest() {
        // given
        GpsLogEntity log = new GpsLogEntity("1234", "37.5665", "126.9780", LocalDateTime.now());

        // when
        GpsLogEntity savedLog = gpsLogRepository.save(log);
        List<GpsLogEntity> logs = gpsLogRepository.findAll();

        // then
        assertNotNull(savedLog.getId());
        assertEquals(1, logs.size());
        assertEquals(log.getCarNumber(), logs.get(0).getCarNumber());
    }
}
