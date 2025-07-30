package com.example._thecore_back.drivelog.application;

import com.example._thecore_back.drivelog.domain.DriveLog;
import com.example._thecore_back.drivelog.domain.DriveLogRepository;
import com.example._thecore_back.drivelog.dto.DriveLogRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DriveLogService {

    private final DriveLogRepository driveLogRepository;

    public DriveLog save(DriveLogRequest request) {
        DriveLog driveLog = DriveLog.builder()
                .carId(request.getCarId())
                .startPoint(request.getStartPoint())
                .startLatitude(request.getStartLatitude())
                .startLongitude(request.getStartLongitude())
                .startTime(request.getStartTime())
                .endPoint(request.getEndPoint())
                .endLatitude(request.getEndLatitude())
                .endLongitude(request.getEndLongitude())
                .endTime(request.getEndTime())
                .driveDist(request.getDriveDist())
                .speed(request.getSpeed())
                .createdAt(LocalDateTime.now()) // 엔티티에서 자동 설정 안하면 필요
                .build();

        return driveLogRepository.save(driveLog);
    }

    public List<DriveLog> getAllLogs() {
        return driveLogRepository.findAll();
    }

    public List<DriveLog> getLogsByCarId(Long carId) {
        return driveLogRepository.findByCarId(carId);
    }

    public List<DriveLog> getLogsBetween(LocalDateTime start, LocalDateTime end) {
        return driveLogRepository.findByStartTimeBetween(start, end);
    }

    public DriveLog getLogById(Long id) {
        return driveLogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 주행기록이 없습니다: " + id));
    }
}
