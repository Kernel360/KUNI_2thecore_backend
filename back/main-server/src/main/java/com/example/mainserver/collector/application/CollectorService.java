package com.example.mainserver.collector.application;

import com.example.mainserver.collector.exception.GpsLogNotFoundException;
import com.example.mainserver.collector.domain.dto.GpsLogDto;
import com.example.mainserver.collector.domain.dto.GpsLogResponseDto;

import com.example.mainserver.collector.infrastructure.rabbitmq.GpsLogProducer;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CollectorService {

//    private final ApplicationEventPublisher eventPublisher;
    private final GpsLogProducer gpsLogProducer;

    public GpsLogResponseDto getGpsLog(GpsLogDto gpsLogDto) {

        if (gpsLogDto.getLogList().isEmpty()) {
            throw new GpsLogNotFoundException();
        }

        // db 저장후 event를 발생시킨다 rabbitmq 메세지 삽입
        gpsLogProducer.sendLogs(gpsLogDto);

        return GpsLogResponseDto.builder()
                .carNumber(gpsLogDto.getCarNumber())
                .build();
    }

}
