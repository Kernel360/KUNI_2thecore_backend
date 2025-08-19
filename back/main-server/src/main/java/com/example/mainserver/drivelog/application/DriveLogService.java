package com.example.mainserver.drivelog.application;

import com.example.mainserver.cache.DriveLogFilterCache;
import com.example.mainserver.drivelog.domain.DriveLog;
import com.example.mainserver.drivelog.domain.DriveLogRepository;
import com.example.mainserver.drivelog.dto.DriveLogFilterRequestDto;
import com.example.mainserver.drivelog.dto.DriveLogFilterResponseDto;
import com.example.mainserver.drivelog.dto.DriveLogRequest;
import com.example.mainserver.drivelog.infrastructure.mapper.DriveLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DriveLogService {

    private final DriveLogRepository driveLogRepository;

    private final DriveLogFilterCache driveLogFilterCache;

    private final DriveLogMapper driveLogMapper;

    public DriveLog save(DriveLogRequest request) {
        // 필수 값 검증 - 예시로 간단히
        if (request.getCarId() == null) {
            throw new IllegalArgumentException("차량 ID는 필수입니다.");
        }
        if (request.getDriveDist() == null) {
            throw new IllegalArgumentException("주행 거리는 필수입니다.");
        }
        // 필요한 추가 검증 가능

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
                .build();

        return driveLogRepository.save(driveLog);
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
