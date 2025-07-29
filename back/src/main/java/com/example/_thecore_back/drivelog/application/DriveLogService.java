package com.example._thecore_back.drivelog.application;

import com.example._thecore_back.drivelog.domain.DriveLog;
import com.example._thecore_back.drivelog.domain.DriveLogRepository;
import com.example._thecore_back.drivelog.dto.DriveLogRequest;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@Service
@RequiredArgsConstructor
public class DriveLogService {

    private final DriveLogRepository driveLogRepository;

    public DriveLog save(DriveLogRequest request) {
        DriveLog driveLog = DriveLog.builder()
                .driveDist(request.getDriveDist())
                .speed(request.getSpeed())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .carId(request.getCarId())
                .locationId(request.getLocationId())
                .build();

        return driveLogRepository.save(driveLog);
    }


    public List<DriveLog> getAllLogs(){
        return driveLogRepository.findAll();
    }

    public List<DriveLog> getLogsByCarId(Long carId){
        return driveLogRepository.findByCarId(carId);
    }

    public List<DriveLog> getLogsBetween(LocalDateTime start, LocalDateTime end){
        return driveLogRepository.findByStartTimeBetween(start, end);
    }

    public DriveLog getLogById(Long id){
        return driveLogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 주행기록이 없습니다: " + id));
    }
}
