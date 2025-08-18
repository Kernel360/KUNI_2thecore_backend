package hub.application;

import hub.domain.GpsLogEntity;
import hub.domain.GpsLogRepository;
import hub.domain.dto.GpsLogDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ConsumerServiceTest {

    @Mock
    private GpsLogRepository gpsLogRepository;

    @InjectMocks
    private ConsumerService consumerService;

    @Test
    @DisplayName("GPS 로그 소비 및 저장 테스트")
    void gpsConsumer_SaveTest() {
        // given
        GpsLogDto.Gps gps = new GpsLogDto.Gps("37.5665", "126.9780", LocalDateTime.now());
        GpsLogDto gpsLogDto = new GpsLogDto("A1234", Collections.singletonList(gps));

        // when
        consumerService.gpsConsumer(gpsLogDto);

        // then
        ArgumentCaptor<List<GpsLogEntity>> captor = ArgumentCaptor.forClass(List.class);
        verify(gpsLogRepository).saveAll(captor.capture());

        List<GpsLogEntity> savedLogs = captor.getValue();
        assertEquals(1, savedLogs.size());

        GpsLogEntity savedLog = savedLogs.get(0);
        assertEquals(gpsLogDto.getCarNumber(), savedLog.getCarNumber());
        assertEquals(gps.getLatitude(), savedLog.getLatitude());
        assertEquals(gps.getLongitude(), savedLog.getLongitude());
        assertEquals(gps.getTimestamp(), savedLog.getCreatedAt());
    }
}
