package com.example.mainserver.drivelog.application;

import com.example.mainserver.cache.DriveLogFilterCache;
import com.example.mainserver.car.application.CarService;
import com.example.mainserver.drivelog.domain.DriveLog;
import com.example.mainserver.drivelog.domain.DriveLogRepository;
import com.example.mainserver.drivelog.dto.DriveLogFilterRequestDto;
import com.example.mainserver.drivelog.dto.DriveLogFilterResponseDto;
import com.example.mainserver.drivelog.dto.DriveLogRequest;
import com.example.mainserver.drivelog.infrastructure.mapper.DriveLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DriveLogService {

    private final DriveLogRepository driveLogRepository;
    private final DriveLogFilterCache driveLogFilterCache;
    private final DriveLogMapper driveLogMapper;
    private final CarService carService;

    @Transactional
    public DriveLog save(DriveLogRequest request) {
        // 필수 값 검증
        if (request.getCarId() == null) {
            throw new IllegalArgumentException("차량 ID는 필수입니다.");
        }

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
                .speed(request.getSpeed())
                .build();

        // driveDist 계산
        driveLog.calculateDriveDist();
        
        log.info("Calculated driveDist for car {}: {} km", 
                request.getCarId(), driveLog.getDriveDist());

        // DriveLog 저장
        DriveLog savedLog = driveLogRepository.save(driveLog);

        // Car의 sumDist 자동 업데이트
        double additionalDistance = driveLog.getDriveDist().doubleValue();
        if (additionalDistance > 0) {
            carService.updateSumDist(request.getCarId(), additionalDistance);
        }

        return savedLog;
    }

    // 차량ID로 현재 진행 중인 드라이브 로그 찾아서 실시간 위치 업데이트
    @Transactional
    public DriveLog updateCurrentDriveLogLocation(Long carId, String newLatitude, String newLongitude) {
        // 현재 진행 중인 드라이브 로그 찾기 (endTime이 null인 가장 최근 기록)
        List<DriveLog> activeLogs = driveLogRepository.findByCarId(carId);
        
        DriveLog currentLog = activeLogs.stream()
                .filter(log -> log.getEndTime() == null)
                .max((log1, log2) -> log1.getStartTime().compareTo(log2.getStartTime()))
                .orElse(null);
        
        if (currentLog == null) {
            log.debug("No active drive log found for car {}", carId);
            return null;
        }
        
        // 실시간 위치 업데이트 및 거리 누적
        double additionalDist = currentLog.updateWithNewLocation(newLatitude, newLongitude);
        log.info("Updated current drive log {} for car {}: +{} km", 
                currentLog.getDriveLogId(), carId, additionalDist);
        
        // 차량 sumDist 업데이트
        if (additionalDist > 0) {
            carService.updateSumDist(carId, additionalDist);
        }
        
        return driveLogRepository.save(currentLog);
    }

    // 실시간 좌표 업데이트 + driveDist 누적 + 차량 sumDist 업데이트
    @Transactional
    public DriveLog addRealTimeLocation(Long driveLogId, String newLatitude, String newLongitude) {
        DriveLog driveLog = driveLogRepository.findById(driveLogId)
                .orElseThrow(() -> new IllegalArgumentException("해당 주행 기록이 없습니다: " + driveLogId));

        // 새 좌표로 거리 계산 후 driveDist 누적
        double additionalDist = driveLog.updateWithNewLocation(newLatitude, newLongitude);
        log.info("Updated driveDist for driveLog {}: +{} km", driveLogId, additionalDist);

        // 차량 sumDist 업데이트
        if (additionalDist > 0){
            carService.updateSumDist(driveLog.getCarId(), additionalDist);
        }

        return driveLog;
    }

    public List<DriveLog> getAllLogs() {
        return driveLogRepository.findAll();
    }

    public List<DriveLog> getLogsByCarId(Long carId) {
        if (carId == null) {
            throw new IllegalArgumentException("차량 ID가 필요합니다.");
        }
        return driveLogRepository.findByCarId(carId);
    }

    public List<DriveLog> getLogsBetween(java.time.LocalDateTime start, java.time.LocalDateTime end) {
        if (start == null || end == null) {
            throw new IllegalArgumentException("조회 시작일과 종료일을 모두 입력해야 합니다.");
        }
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("시작일은 종료일보다 이전이어야 합니다.");
        }
        return driveLogRepository.findByStartTimeBetween(start, end);
    }

    public DriveLog getLogById(Long id) {
        return driveLogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 주행기록이 없습니다: " + id));
    }

    public Page<DriveLogFilterResponseDto> getDriveLogByFilter(DriveLogFilterRequestDto driveLogFilterRequestDto,
                                                               int page, int size) {
        int offset = (page - 1) * size;

        var result = driveLogMapper.search(driveLogFilterRequestDto, offset, size);

        var total = driveLogMapper.countByFilter(driveLogFilterRequestDto);


        return new PageImpl<>(result, PageRequest.of(page - 1, size), total);
    }
}
