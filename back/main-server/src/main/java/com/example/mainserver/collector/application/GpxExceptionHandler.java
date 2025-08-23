package com.example.mainserver.collector.application;

import com.example.mainserver.collector.domain.dto.GpsLogDto.Gps;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class GpxExceptionHandler {

    // 차량별 마지막 GPS 로그와 프리즈 카운트를 저장하는 맵
    private final Map<String, Gps> previousGpxLogMap = new ConcurrentHashMap<>();
    private final Map<String, Integer> freezeCountMap = new ConcurrentHashMap<>();


    private static final int FREEZE_THRESHOLD = 10; // 10초(라인) 이상 동일 좌표일 경우 프리즈로 간주
    private static final double SPIKE_DISTANCE_THRESHOLD_METER = 140; // 1초당 140m 이동(약 500km/h)을 스파이크로 간주
    private static final long DELETION_THRESHOLD_SECONDS = 2; // 2초 이상 차이나면 삭제로 간주

    /**
     * 현재 좌표가 이전 좌표와 비교하여 '위치 스파이크'에 해당하는지 확인
     * @param currentGpxLog 현재 GPX 데이터 포인트
     * @return 스파이크인 경우 true
     */
    public boolean isSpike(Gps currentGpxLog, String carNumber) {
        Gps previousGpxLog = previousGpxLogMap.get(carNumber);
        if (previousGpxLog == null) {
            return false;
        }

        double distance = calculateDistance(
                Double.parseDouble(previousGpxLog.getLatitude()),
                Double.parseDouble(previousGpxLog.getLongitude()),
                Double.parseDouble(currentGpxLog.getLatitude()),
                Double.parseDouble(currentGpxLog.getLongitude())
        );

        if (distance > SPIKE_DISTANCE_THRESHOLD_METER) {
            log.error("[Location Spike Detected] carNumber: {}, distance: {}m", carNumber, String.format("%.2f", distance));
            return true;
        }
        return false;
    }

    /**
     * 현재 좌표가 이전 좌표와 비교하여 '신호 끊김(프리즈)'에 해당하는지 확인
     * @param currentGpxLog 현재 GPX 데이터 포인트
     * @return 프리즈 상태인 경우 true
     */
    public boolean isFreeze(Gps currentGpxLog, String carNumber) {
        Gps previousGpxLog = previousGpxLogMap.get(carNumber);
        int freezeCount = freezeCountMap.getOrDefault(carNumber, 0);

        if (previousGpxLog != null &&
                previousGpxLog.getLatitude().equals(currentGpxLog.getLatitude()) &&
                previousGpxLog.getLongitude().equals(currentGpxLog.getLongitude())) {
            freezeCount++;
        } else {
            // 좌표가 변경되면 카운터 리셋
            freezeCount = 0;
        }
        freezeCountMap.put(carNumber, freezeCount);


        if (freezeCount >= FREEZE_THRESHOLD) {
            log.warn("[Signal Freeze Detected] carNumber: {}, freezeCount: {}", carNumber, freezeCount);
            return true;
        }
        return false;
    }

    /**
     * 현재 좌표와 이전 좌표의 시간 차이를 비교하여 '신호 끊김(삭제)'에 해당하는지 확인
     * @param currentGpxLog 현재 GPX 데이터 포인트
     * @return 삭제로 판단되는 경우 true
     */
    public boolean isDeletion(Gps currentGpxLog, String carNumber) {
        Gps previousGpxLog = previousGpxLogMap.get(carNumber);
        if (previousGpxLog == null) {
            return false;
        }

        try {
            LocalDateTime prevTime = previousGpxLog.getTimestamp();
            LocalDateTime currentTime = currentGpxLog.getTimestamp();

            long secondsBetween = Duration.between(prevTime, currentTime).getSeconds();

            if (secondsBetween > DELETION_THRESHOLD_SECONDS) {
                log.warn("[Signal Deletion Detected] carNumber: {}, Time gap: {} seconds", carNumber, secondsBetween);
                return true;
            }
        } catch (Exception e) {
            log.error("Timestamp comparison error", e);
            return false;
        }
        return false;
    }


    /**
     * 유효한 현재 포인트를 다음 비교를 위해 이전 포인트로 업데이트
     * @param currentGpxLog 유효하다가 판단된 현재 GPX 데이터 포인트
     */
    public void updatePreviousPoint(Gps currentGpxLog, String carNumber) {
        previousGpxLogMap.put(carNumber, currentGpxLog);
    }

    /**
     * 특정 차량의 GPX 처리 상태를 초기화
     */
    public void reset(String carNumber) {
        previousGpxLogMap.remove(carNumber);
        freezeCountMap.remove(carNumber);
    }

    /**
     * 두 위도/경도 좌표 간의 거리를 미터(m) 단위로 계산
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double earthRadius = 6371000; // meters
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return earthRadius * c;
    }
}
