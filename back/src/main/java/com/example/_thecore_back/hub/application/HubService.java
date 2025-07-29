package com.example._thecore_back.hub.application;

import com.example._thecore_back.emulator.domain.EmulatorEntity;
import com.example._thecore_back.emulator.infrastructure.EmulatorReaderImpl;
import com.example._thecore_back.emulator.infrastructure.EmulatorRepository;
import com.example._thecore_back.hub.domain.GpsLogConverter;
import com.example._thecore_back.hub.domain.GpsLogEntity;
import com.example._thecore_back.hub.domain.GpsLogWriter;
import com.example._thecore_back.hub.domain.dto.GpsLogDto;
import com.example._thecore_back.hub.domain.dto.GpsLogResponseDto;
import com.example._thecore_back.hub.infrastructure.GpsLogWriterImpl;
import com.example._thecore_back.hub.infrastructure.rabbitmq.GpsLogProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HubService {

    private final EmulatorReaderImpl emulatorReader;
    private final GpsLogWriterImpl gpsLogWriterImpl;
    private final GpsLogConverter gpsLogConverter;
    private final GpsLogProducer gpsLogProducer;


    public GpsLogResponseDto getGpsLog(GpsLogDto gpsLogDto) {

        EmulatorEntity emulator = emulatorReader.findByCarNumber(gpsLogDto.getCarNumber())
                .orElseThrow(() -> new RuntimeException("해당 차량의 에뮬레이터는 존재하지 않습니다."));

        List<GpsLogEntity> logs = gpsLogDto.getLogList().stream()
                .map(dto -> gpsLogConverter.toEntityByEmulatorId(dto,emulator.getId()))
                .toList();

        gpsLogWriterImpl.saveAll(logs);


        // rabbitMq produce로 전송
        gpsLogProducer.sendLogs(gpsLogDto);

        return GpsLogResponseDto.builder()
                .deviceId(emulator.getDeviceId())
                .startTime(gpsLogDto.getStartTime())
                .endTime(gpsLogDto.getEndTime())
                .build();
    }

}
