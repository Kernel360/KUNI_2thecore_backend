package com.example.emulatorserver.device.application;

import com.example.emulatorserver.device.application.dto.GpxLogDto;
import com.example.emulatorserver.device.application.dto.GpxRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GpxSchedulerTest {

    private GpxScheduler scheduler;

    @BeforeEach
    void setUp() {
        scheduler = new GpxScheduler();
        scheduler.setRestTemplate(new RestTemplate());
        scheduler.setCarNumber("12가3456");
        scheduler.setLoginId("testUser");
    }

    @Test
    void initTest() {
        // When
        scheduler.init();

        // Then
        List<String> gpxLines = scheduler.getGpxFile();
        assertNotNull(gpxLines, "gpxFile이 null입니다.");
        assertFalse(gpxLines.isEmpty(), "gpx 파일이 비어 있습니다.");

        // gpxFile 내용 출력
        System.out.println("gpxFile 내용:");
        gpxLines.forEach(System.out::println);

        List<GpxLogDto> buffer = scheduler.getBuffer();
        assertNotNull(buffer, "buffer가 null입니다.");
    }

    @Test
    void startSchedulerTest() throws InterruptedException {
        // given
        scheduler.init();

        // init 대기
        Thread.sleep(10000);

        // then
        List<GpxLogDto> buffer = scheduler.getBuffer();
        assertNotNull(buffer);
        assertFalse(buffer.isEmpty(), "스케줄러 실행 후 buffer가 비어 있습니다.");
        buffer.forEach(log -> {
            System.out.println("timestamp: " + log.getTimeStamp()
                    + ", lat: " + log.getLatitude()
                    + ", lon: " + log.getLongitude());
        });
    }

    @Test
    void stopSchedulerTest() throws InterruptedException {
        // given
        scheduler.init();

        // when
        scheduler.stopScheduler();
        Thread.sleep(500);

        // then
        assertTrue(scheduler.getScheduler().isShutdown(), "스케줄러가 정상적으로 종료되지 않았습니다.");
    }


    @Test
    void sendGpxDataTest() {
        // given
        RestTemplate mockRestTemplate = Mockito.mock(RestTemplate.class);
        scheduler.setRestTemplate(mockRestTemplate);

        // testLog 추가
        GpxLogDto testLog = GpxLogDto.builder()
                .timeStamp("2025-07-30T12:00:00")
                .latitude("37.1234")
                .longitude("127.5678")
                .build();
        scheduler.getBuffer().add(testLog);

        scheduler.setStartTime("2025-07-30T12:00:00");
        scheduler.setEndTime("2025-07-30T12:01:00");

        Mockito.when(mockRestTemplate.postForEntity(Mockito.anyString(), Mockito.any(), Mockito.eq(String.class)))
                .thenReturn(ResponseEntity.ok("success"));

        // when
        scheduler.sendGpxData();

        // then
        ArgumentCaptor<HttpEntity> captor = ArgumentCaptor.forClass(HttpEntity.class);
        Mockito.verify(mockRestTemplate).postForEntity(
                Mockito.eq("http://localhost:8080/api/logs/gps"),
                captor.capture(),
                Mockito.eq(String.class)
        );

        HttpEntity<?> entity = captor.getValue();
        assertNotNull(entity);
        assertEquals(org.springframework.http.MediaType.APPLICATION_JSON, entity.getHeaders().getContentType());

        Object body = entity.getBody();
        System.out.println("body: " + body);

        assertInstanceOf(GpxRequestDto.class, body);
        GpxRequestDto dto = (GpxRequestDto) body;

        assertEquals("12가3456", dto.getCarNumber());
        assertEquals("testUser", dto.getLoginId());
        assertEquals(0, dto.getLogList().size());

        assertTrue(scheduler.getBuffer().isEmpty(), "데이터 전송 후 buffer가 비워지지 않았습니다.");
    }
}
