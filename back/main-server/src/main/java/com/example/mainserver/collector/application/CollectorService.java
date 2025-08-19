package com.example.mainserver.collector.application;

import com.example.mainserver.collector.exception.GpsLogNotFoundException;
import com.example.mainserver.collector.domain.dto.GpsLogDto;
import com.example.mainserver.collector.domain.dto.GpsLogResponseDto;

import com.example.mainserver.collector.infrastructure.rabbitmq.GpsLogProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
@Slf4j
public class CollectorService {

    private final GpsLogProducer gpsLogProducer;

    private final RestTemplate restTemplate;


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

    public GpsLogResponseDto getGpsLogDirect(GpsLogDto gpsLogDto) {
        if (gpsLogDto.getLogList().isEmpty()) {
            throw new GpsLogNotFoundException();
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<GpsLogDto> request = new HttpEntity<>(gpsLogDto, headers);

        log.info(request.toString());

        var url = "http://52.78.122.150:8082/api/hub/gps-direct";

        var response = restTemplate.postForEntity(url, request, String.class);

        log.info(response.toString());

        return GpsLogResponseDto.builder()
                .carNumber(gpsLogDto.getCarNumber())
                .build();
    }
}
