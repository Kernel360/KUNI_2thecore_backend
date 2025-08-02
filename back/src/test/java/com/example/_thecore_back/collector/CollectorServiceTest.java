package com.example._thecore_back.collector;


import com.example._thecore_back.car.application.CarService;
import com.example._thecore_back.collector.application.CollectorService;
import com.example._thecore_back.collector.domain.GpsLogConverter;
import com.example._thecore_back.collector.domain.GpsLogEntity;
import com.example._thecore_back.collector.domain.GpsLogEvent;
import com.example._thecore_back.collector.domain.dto.GpsLogDto;
import com.example._thecore_back.collector.exception.CollectorEmulatorNotFoundException;
import com.example._thecore_back.collector.exception.GpsLogNotFoundException;
import com.example._thecore_back.collector.infrastructure.GpsLogWriterImpl;
import com.example.emulatorserver.device.domain.emulator.EmulatorEntity;
import com.example.emulatorserver.device.infrastructure.emulator.EmulatorReaderImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import static org.junit.jupiter.api.Assertions.assertEquals;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class CollectorServiceTest {

    @Mock
    private EmulatorReaderImpl emulatorReader;

    @Mock
    private GpsLogWriterImpl gpsLogWriterImpl;

    @Mock
    private GpsLogConverter gpsLogConverter;

    @Mock
    private CarService carService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @InjectMocks
    private CollectorService collectorService;

    @Test
    @DisplayName("Service에서 로그들을 처리하고 응답 반환 테스트")
    public void testCollectorService(){

        LocalDateTime now = LocalDateTime.of(2025, 7, 31, 12, 0);
        GpsLogDto.Gps gps = new GpsLogDto.Gps("1.23", "4.56", now);
        GpsLogDto gpsLogdto = new GpsLogDto("12가1234", "user", List.of(gps), now, now.plusMinutes(1));

        var emulator = EmulatorEntity.builder()
                .id(1)
                .carNumber("12가1234")
                .deviceId("1234")
                .build();

        var gpsLogEntity =  GpsLogEntity.builder()
                .emulatorId(1)
                .gpsLatitude("1.23")
                .gpsLongitude("4.56")
                .createdAt(now.plusMinutes(1))
                .build();

        when(emulatorReader.findByCarNumber("12가1234")).thenReturn(Optional.of(emulator));
        when(gpsLogConverter.toEntityByEmulatorId(gps, 1)).thenReturn(gpsLogEntity);

        var result = collectorService.getGpsLog(gpsLogdto);

        assertEquals("1234", result.getDeviceId());
        assertEquals(now, result.getStartTime());
        assertEquals(now.plusMinutes(1), result.getEndTime());


        verify(gpsLogWriterImpl).saveAll(anyList());
        verify(carService).updateLastLocation("12가1234", "1.23", "4.56");
        verify(eventPublisher).publishEvent(any(GpsLogEvent.class));
    }

    @Test
    @DisplayName("GPS 로그가 없을때")
    public void testGpsLogNotFound(){
        var gpsLogDto = new GpsLogDto("12가1234", "admin", List.of(), LocalDateTime.now(), LocalDateTime.now().plusMinutes(1));

        assertThrows(GpsLogNotFoundException.class, () -> {collectorService.getGpsLog(gpsLogDto);});
    }


    @Test
    @DisplayName("해당 차량의 에뮬레이터가 존재하지 않음")
    public void testEmulatorNotFound(){
        GpsLogDto.Gps gps = new GpsLogDto.Gps("1.23", "4.56", LocalDateTime.now());
        GpsLogDto gpsLogDto = new GpsLogDto(
                "11111111", "loginId", List.of(gps),
                LocalDateTime.now(), LocalDateTime.now().plusMinutes(1)
        );

        when(emulatorReader.findByCarNumber("11111111")).thenReturn(Optional.empty());

        assertThrows(CollectorEmulatorNotFoundException.class, () -> {collectorService.getGpsLog(gpsLogDto);});

    }

}
