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

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CollectorService {

    private final GpsLogProducer gpsLogProducer;
    private final RestTemplate restTemplate;
    private final GpxExceptionHandler gpxExceptionHandler; // 싱글톤 핸들러 주입

    public GpsLogResponseDto getGpsLog(GpsLogDto gpsLogDto) {

        if (gpsLogDto.getLogList().isEmpty()) {
            throw new GpsLogNotFoundException();
        }

        String carNumber = gpsLogDto.getCarNumber();
        List<GpsLogDto.Gps> validGpsData = new ArrayList<>();

        for (GpsLogDto.Gps gps : gpsLogDto.getLogList()) {
            // Deletion은 이벤트 감지용으로만 사용하며, 데이터 자체를 필터링하지 않음
            gpxExceptionHandler.isDeletion(gps, carNumber);

            // Spike나 Freeze가 아닌 유효한 데이터만 리스트에 추가
            if (!gpxExceptionHandler.isSpike(gps, carNumber) && !gpxExceptionHandler.isFreeze(gps, carNumber)) {
                validGpsData.add(gps);
            }
            // 다음 비교를 위해 항상 현재 포인트를 이전 포인트로 업데이트
            gpxExceptionHandler.updatePreviousPoint(gps, carNumber);
        }

        gpsLogDto.setLogList(validGpsData);

        log.info("gpslog가 발행되었습니다. :{}", gpsLogDto);
        // db 저장후 event를 발생시킨다 rabbitmq 메세지 삽입
        gpsLogProducer.sendLogs(gpsLogDto);

        log.info("gpslog가 RabbitMQ에 발행되었습니다. : {}", gpsLogDto);

        return GpsLogResponseDto.builder()
                .carNumber(gpsLogDto.getCarNumber())
                .build();
    }

    public GpsLogResponseDto getGpsLogDirect(GpsLogDto gpsLogDto) {
        if (gpsLogDto.getLogList().isEmpty()) {
            throw new GpsLogNotFoundException();
        }

        String carNumber = gpsLogDto.getCarNumber();
        List<GpsLogDto.Gps> validGpsData = new ArrayList<>();

        for (GpsLogDto.Gps gps : gpsLogDto.getLogList()) {
            // Deletion은 이벤트 감지용으로만 사용하며, 데이터 자체를 필터링하지 않음
            gpxExceptionHandler.isDeletion(gps, carNumber);

            // Spike나 Freeze가 아닌 유효한 데이터만 리스트에 추가
            if (!gpxExceptionHandler.isSpike(gps, carNumber) && !gpxExceptionHandler.isFreeze(gps, carNumber)) {
                validGpsData.add(gps);
            }
            // 다음 비교를 위해 항상 현재 포인트를 이전 포인트로 업데이트
            gpxExceptionHandler.updatePreviousPoint(gps, carNumber);
        }

        gpsLogDto.setLogList(validGpsData);

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
