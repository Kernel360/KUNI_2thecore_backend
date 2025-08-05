package com.example.mainserver.collector.application;

import com.example.common.domain.emulator.EmulatorEntity;
import com.example.common.domain.emulator.EmulatorReader;
import com.example.mainserver.car.application.CarService;
import com.example.mainserver.collector.domain.GpsLogEvent;
import com.example.mainserver.collector.exception.CollectorEmulatorNotFoundException;
import com.example.mainserver.collector.exception.GpsLogNotFoundException;
import com.example.mainserver.collector.domain.GpsLogConverter;
import com.example.mainserver.collector.domain.GpsLogEntity;
import com.example.mainserver.collector.domain.dto.GpsLogDto;
import com.example.mainserver.collector.domain.dto.GpsLogResponseDto;
import com.example.mainserver.collector.infrastructure.GpsLogWriterImpl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CollectorService {

    private final EmulatorReader emulatorReader;

    private final GpsLogWriterImpl gpsLogWriterImpl;
    private final GpsLogConverter gpsLogConverter;
    private final CarService carService;

    private final ApplicationEventPublisher eventPublisher;


    @Transactional
    public GpsLogResponseDto getGpsLog(GpsLogDto gpsLogDto) {

        List<GpsLogDto.Gps> logList = gpsLogDto.getLogList();

        if (logList.isEmpty()) {
            throw new GpsLogNotFoundException();
        }

        EmulatorEntity emulator = emulatorReader.findByCarNumber(gpsLogDto.getCarNumber())
                .orElseThrow(() -> new CollectorEmulatorNotFoundException(gpsLogDto.getCarNumber()));

        List<GpsLogEntity> logs = logList.stream()
                .map(dto -> gpsLogConverter.toEntityByEmulatorId(dto,emulator.getId()))
                .toList();


        var lastestLog = gpsLogDto.getLogList().stream()
                .max(Comparator.comparing(GpsLogDto.Gps::getTimestamp))
                .orElseThrow(() -> new RuntimeException("최신 기록이 존재하지 않습니다."));


        // gps 로그들은 db에 저장
        gpsLogWriterImpl.saveAll(logs);


        // 로그를 기준으로 차량의 현재 위치의 위도, 경도 저장
        carService.updateLastLocation(gpsLogDto.getCarNumber(), lastestLog.getLatitude(), lastestLog.getLongitude());


        // db 저장후 event를 발생시킨다 rabbitmq 메세지 삽입
        eventPublisher.publishEvent(new GpsLogEvent(gpsLogDto));




        return GpsLogResponseDto.builder()
                .deviceId(emulator.getDeviceId())
                .startTime(gpsLogDto.getStartTime())
                .endTime(gpsLogDto.getEndTime())
                .build();
    }

}
