package hub.application;

import hub.domain.GpsWebSocketHandler;
import hub.domain.dto.GpsLogDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsumerServiceTest {

    @Mock
    private GpsWebSocketHandler gpsWebSocketHandler;

    @InjectMocks
    private ConsumerService consumerService;

    @Test
    @DisplayName("GPS 데이터 소비 성공")
    void gpsConsumer_Success() {
        GpsLogDto.Gps gps = new GpsLogDto.Gps("37.5665", "126.9780", LocalDateTime.now());
        GpsLogDto gpsLogDto = new GpsLogDto("user123", "1234", Collections.singletonList(gps));

        consumerService.gpsConsumer(gpsLogDto);

        verify(gpsWebSocketHandler, times(1)).sendGpsLogToClient(gpsLogDto);
    }

    @Test
    @DisplayName("GPS 데이터 소비 시 핸들러 예외 발생")
    void gpsConsumer_HandlerThrowsException() {
        GpsLogDto.Gps gps = new GpsLogDto.Gps("37.5665", "126.9780", LocalDateTime.now());
        GpsLogDto gpsLogDto = new GpsLogDto("user123", "1234", Collections.singletonList(gps));
        doThrow(new RuntimeException("Test Exception")).when(gpsWebSocketHandler).sendGpsLogToClient(any(GpsLogDto.class));

        assertThrows(RuntimeException.class, () -> consumerService.gpsConsumer(gpsLogDto));

        verify(gpsWebSocketHandler, times(1)).sendGpsLogToClient(gpsLogDto);
    }
}
