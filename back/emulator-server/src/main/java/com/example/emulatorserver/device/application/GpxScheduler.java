package com.example.emulatorserver.device.application;

import com.example.emulatorserver.device.application.dto.GpxLogDto;
import com.example.emulatorserver.device.application.dto.GpxRequestDto;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
@Setter
@Getter
public class GpxScheduler{
    @Autowired
    private RestTemplate restTemplate; // api 호출을 위함

    private List<String> gpxFile = new ArrayList<>(); // Gpx 파일을 읽어와 저장해두는 리스트
    private List<GpxLogDto> buffer = new ArrayList<>(); // 전송될 GPX 정보들을 저장해두는 리스트
    private int currentIndex = 0; // 읽어야 할 line 번호

    private String carNumber;
    private String loginId;
    private String startTime;
    private String endTime;

    // 스케줄러 실행 여부 확인
    private ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    @PostConstruct
    public void init() { // init method:  랜덤한 GPX 파일 로드 후 메모리(gpxFile)에 로드
        try {
            // classpath 내 /gpx 폴더 경로 가져오기
            var resourceUrl = getClass().getClassLoader().getResource("gpx");
            if (resourceUrl == null) {
                log.error("gpx 폴더를 찾을 수 없습니다.");
                return;
            }

            File dir = new File(resourceUrl.toURI());
            File[] files = dir.listFiles();
            if (files == null || files.length == 0) {
                log.error("gpx 폴더 내에 파일이 없습니다.");
                return;
            }

            // 랜덤 파일 선택
            File randomFile = files[new Random().nextInt(files.length)];
            log.info("선택된 GPX 파일: {}", randomFile.getName());

            // 파일을 한 줄씩 읽어 gpxFile 리스트에 저장 - 위/경도 정보만 필터링
            gpxFile = Files.readAllLines(randomFile.toPath()).stream()
                    .filter(line -> line.contains("<trkpt"))
                    .collect(Collectors.toList());
            buffer.clear();
            currentIndex = 0;

            startScheduler();

        } catch (Exception e) {
            log.error("GPX 파일 로드 중 오류 발생", e);
        }
    }

    // 스케줄러 시작 메서드 - 이미 실행 중이면 재시작하지 않음
    public void startScheduler() {
        Runnable task = () -> {
            try {
                if (carNumber != null && currentIndex < gpxFile.size()) {
                    Pattern pattern = Pattern.compile("lat=\"(.*?)\"\\s+lon=\"(.*?)\""); // 위도 경도 추출을 위한 정규표현식
                    Matcher matcher = pattern.matcher(gpxFile.get(currentIndex));
                    if (matcher.find()) { // Gpx라인을 Dto로 가공하여 리스트에 삽입
                        String timestamp = LocalDateTime.now()
                                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"));
                        String latitude = matcher.group(1);
                        String longitude = matcher.group(2);

                        GpxLogDto dto = GpxLogDto.builder()
                                .timeStamp(timestamp)
                                .latitude(latitude)
                                .longitude(longitude)
                                .build();

                        buffer.add(dto);
                    }

                    if (currentIndex % 60 == 0 && currentIndex != 0) { // 전송 주기가 되면 데이터 전송 함수 실행
                        // Todo DB에 주기 정보 업데이트 후 주기 DB에서 불러오는 코드로 수정하기
                        sendGpxData();
                    }

                    currentIndex++;
                } else {
                    scheduler.shutdown(); // 조건 종료 시 스케줄러 중단
                    log.info("GPX 파일 전송 완료 혹은 차량 정보 없음으로 종료됨");
                }
            } catch (Exception e) {
                log.error("GPX 재생 중 오류 발생", e);
            }
        };

        // 종료된 경우 스레드 풀 새로 생성
        if (scheduler == null || scheduler.isShutdown() || scheduler.isTerminated()) {
            scheduler = Executors.newSingleThreadScheduledExecutor();
        }
        // 일정 간격으로 GPX 데이터 전송 작업 실행
        scheduler.scheduleAtFixedRate(task, 0, 1, TimeUnit.SECONDS);
    }

    // 스케줄러 종료 메서드
    public void stopScheduler() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
            // 스케줄러 상태 초기화
            log.info("GPX 스케줄러가 중단되었습니다.");
        } else {
            log.warn("스케줄러가 이미 종료되었거나 시작되지 않았습니다.");
        }
    };

    protected void sendGpxData() {
        startTime = buffer.get(0).getTimeStamp();
        endTime = buffer.get(buffer.size() - 1).getTimeStamp();

        GpxRequestDto logJson = GpxRequestDto.builder()
                .carNumber(carNumber)
                .loginId(loginId)
                .startTime(startTime)
                .endTime(endTime)
                .locations(buffer)
                .build(); // buffer 내부 로그들 Json화

        // 전송할 Collector API 주소
        String collectorUrl = "http://localhost:8080/api/logs/gps"; // 실제 주소로 변경해야 함

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<GpxRequestDto> request = new HttpEntity<>(logJson, headers);

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(collectorUrl, request, String.class);
            log.info("Collector 응답 상태: {}", response.getStatusCode());
            log.info("Collector 응답 바디: {}", response.getBody());
        } catch (HttpStatusCodeException e) {
            log.error("Collector 서버 오류: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Collector API 호출 실패", e);
        }

        buffer.clear();
    }
}
