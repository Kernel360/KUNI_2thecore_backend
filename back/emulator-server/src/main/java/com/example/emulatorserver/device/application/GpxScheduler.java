package com.example.emulatorserver.device.application;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.BooleanUtils.forEach;

@Component
@Slf4j
public class GpxScheduler {

    private List<String> gpxFile = new ArrayList<>(); // Gpx 파일을 읽어와 저장해두는 리스트
    private List<List<String>> buffer = new ArrayList<>(); // 전송될 GPX 정보들을 저장해두는 리스트
    private int currentIndex = 0; // 읽어야 할 line 번호

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

        } catch (Exception e) {
            log.error("GPX 파일 로드 중 오류 발생", e);
        }
    }

    @Scheduled(fixedRate = 1000)
    public void scheduled() { // 1초마다 한 줄씩 읽어 버퍼 리스트에 저장, 설정된 주기가 되면 데이터 전송
        if (currentIndex >= gpxFile.size()) { // 준비된 gpx파일을 다 읽어왔을 경우
            log.error("gpx 파일을 전부 읽어왔습니다.");
            return;
        }

        if (currentIndex % 60 == 0 && currentIndex != 0) { // 주기가 되었을 경우
            // Todo 주기 값 디비에서 가져오기 - DB 업데이트 후
            sendGpxData();
        }

        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String line = gpxFile.get(currentIndex);
        List<String> data = List.of(timestamp, line);
        buffer.add(data);
        System.out.println(data);
        System.out.println("buffer: " + buffer.size());
        currentIndex++;
    }

    private void sendGpxData() {
        // Todo 데이터 전송 - Json화 후 API 사용하여 collector server로 전송
        // 임시 코드
        System.out.println("전송!!!!!!!!!!1");
        buffer.clear();
    }

    private void convertBufferToJson() {
        // Todo buffer리스트를 전송할 데이터 형식으로 변형
    }
}
