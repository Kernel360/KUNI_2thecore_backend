package com.example.mainserver.drivelog.application;

import com.example.common.domain.car.CarEntity;
import com.example.common.infrastructure.car.CarReaderImpl;
import com.example.mainserver.cache.DriveLogFilterCache;
import com.example.mainserver.car.exception.CarErrorCode;
import com.example.mainserver.car.exception.CarNotFoundException;
import com.example.mainserver.drivelog.domain.DriveLog;
import com.example.mainserver.drivelog.domain.DriveLogRepository;
import com.example.mainserver.drivelog.dto.*;
import com.example.mainserver.drivelog.infrastructure.mapper.DriveLogMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DriveLogService {

    private final DriveLogRepository driveLogRepository;

    private final DriveLogFilterCache driveLogFilterCache;

    private final DriveLogMapper driveLogMapper;

    private final CarReaderImpl carReader;

    private final ReverseGeoCodingService reverseGeoCodingService;

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

    public DriveLog startDrive(StartDriveRequestDto request){
        if(request.getCarNumber() == null) {
            throw new IllegalArgumentException("차량 번호는 필수입니다.");
        }

        Integer carIdInt = carReader.getIdfromNumber(request.getCarNumber());
        if (carIdInt == null) {
            throw new CarNotFoundException(CarErrorCode.CAR_NOT_FOUND_BY_NUMBER, request.getCarNumber());
        }
        Long carId = carIdInt.longValue();
        CarEntity drivingCar = carReader.findByCarNumber(request.getCarNumber()).get();

        // 위도 경도 값을 역지오코딩을 통해 startPoint로 변환
        String startPoint = reverseGeoCodingService.reverseGeoCoding(request.getStartLongitude(), request.getStartLatitude());

        // 주행기록 생성
        DriveLog driveLog = DriveLog.builder()
                .carId(carId)
                .startLatitude(request.getStartLatitude())
                .startLongitude(request.getStartLongitude())
                .startTime(request.getStartTime())
                .startPoint(startPoint)
                .driveDist(BigDecimal.valueOf(0))
                .model(drivingCar.getModel())
                .brand(drivingCar.getBrand())
                .build();
        return driveLogRepository.save(driveLog);
    }

    public DriveLog endDrive(EndDriveRequestDto request) {
        if (request.getCarNumber() == null) {
            throw new IllegalArgumentException("차량 번호는 필수입니다.");
        }

        Long carId = Long.valueOf(carReader.getIdfromNumber(request.getCarNumber()));
        DriveLog driveLog = driveLogRepository.findByCarIdAndStartTime(carId, request.getStartTime());

        if (driveLog == null) {
            throw new IllegalArgumentException("해당 차량의 진행 중 주행기록이 없습니다: " + request.getCarNumber());
        }

        // 위도 경도 endpoint(역지오코딩)
        String endPoint = reverseGeoCodingService.reverseGeoCoding(request.getEndLongitude(), request.getEndLatitude());

        // end 정보 삽입
        driveLog.setEndLatitude(request.getEndLatitude());
        driveLog.setEndLongitude(request.getEndLongitude());
        driveLog.setEndTime(request.getEndTime());
        driveLog.setEndPoint(endPoint);

        return driveLog;
    }
}
