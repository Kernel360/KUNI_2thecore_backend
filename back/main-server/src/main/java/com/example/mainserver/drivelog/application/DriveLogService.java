package com.example.mainserver.drivelog.application;

import com.example.common.domain.car.CarEntity;
import com.example.common.infrastructure.car.CarReaderImpl;
import com.example.mainserver.cache.DriveLogFilterCache;
import com.example.mainserver.car.application.CarService;
import com.example.mainserver.car.exception.CarErrorCode;
import com.example.mainserver.car.exception.CarNotFoundException;
import com.example.mainserver.drivelog.domain.DriveLog;
import com.example.mainserver.drivelog.domain.DriveLogRepository;
import com.example.mainserver.drivelog.dto.*;
import com.example.mainserver.drivelog.infrastructure.mapper.DriveLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.OutputStream;
import java.util.List;
import java.math.BigDecimal;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DriveLogService {

    private final DriveLogRepository driveLogRepository;
    private final DriveLogFilterCache driveLogFilterCache;
    private final DriveLogMapper driveLogMapper;
    private final CarService carService;
    private final CarReaderImpl carReader;
    private final ReverseGeoCodingService reverseGeoCodingService;

    @Transactional
    public DriveLog save(DriveLogRequest request) {
        // 필수 값 검증
        if (request.getCarId() == null) {
            throw new IllegalArgumentException("차량 ID는 필수입니다.");
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

    public List<DriveLogFilterResponseDto> getFilteredDriveLogs(DriveLogFilterRequestDto filterDto) {
        // 페이징 없이 전체 조회 (Excel용)
        return driveLogMapper.search(filterDto, 0, Integer.MAX_VALUE);
    }

    @Transactional
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

    @Transactional
    public DriveLog endDrive(EndDriveRequestDto request) {
        if (request.getCarNumber() == null) {
            throw new IllegalArgumentException("차량 번호는 필수입니다.");
        }

        Long carId = Long.valueOf(carReader.getIdfromNumber(request.getCarNumber()));
        
        // 활성 로그 찾기 (startTime 방식과 동일)
//        DriveLog driveLog = driveLogRepository.findActiveLogByCarId(carId)
//                .orElse(null);

        Optional<DriveLog> currDriveLog = driveLogRepository.findFirstByCarIdAndEndTimeIsNullOrderByStartTimeDesc(carId.intValue());

        if (currDriveLog.isEmpty()) {
            throw new IllegalArgumentException("해당 차량의 진행 중 주행기록이 없습니다: " + request.getCarNumber());
        }

        var driveLog = currDriveLog.get();

        log.info("Found active drive log for car {}: driveLogId={}, startTime={}",
                request.getCarNumber(), driveLog.getDriveLogId(), driveLog.getStartTime());

        // 위도 경도 endpoint(역지오코딩)
        String endPoint = reverseGeoCodingService.reverseGeoCoding(request.getEndLongitude(), request.getEndLatitude());

        // end 정보 삽입 (startTime 방식과 동일)
        driveLog.setEndLatitude(request.getEndLatitude());
        driveLog.setEndLongitude(request.getEndLongitude());
        driveLog.setEndTime(request.getEndTime());
        driveLog.setEndPoint(endPoint);

        // 주행 종료 시에는 실시간으로 이미 누적된 거리를 유지
        // calculateDriveDist()를 호출하지 않음 - 실시간 누적 거리 보존

        DriveLog savedLog = driveLogRepository.save(driveLog);

        log.info("Drive ended for car {}: endTime={}", request.getCarNumber(), savedLog.getEndTime());

        return savedLog;
    }

    public void writeDriveLogsToExcel(OutputStream os, List<DriveLogFilterResponseDto> dtos) throws Exception {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("주행기록");
            String[] headers = {"차량번호", "브랜드", "모델", "주행시작일", "주행종료일", "출발지", "도착지", "주행거리", "상태", "메모"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                headerRow.createCell(i).setCellValue(headers[i]);
            }
            for (int rowIdx = 0; rowIdx < dtos.size(); rowIdx++) {
                DriveLogFilterResponseDto dto = dtos.get(rowIdx);
                Row row = sheet.createRow(rowIdx + 1);
                row.createCell(0).setCellValue(dto.getCarNumber());
                row.createCell(1).setCellValue(dto.getBrand());
                row.createCell(2).setCellValue(dto.getModel());
                row.createCell(3).setCellValue(dto.getStartTime() != null ? dto.getStartTime().toString() : "");
                row.createCell(4).setCellValue(dto.getEndTime() != null ? dto.getEndTime().toString() : "");
                row.createCell(5).setCellValue(dto.getStartPoint());
                row.createCell(6).setCellValue(dto.getEndPoint());
                row.createCell(7).setCellValue(dto.getDriveDist());
                row.createCell(8).setCellValue(dto.getStatus() != null ? dto.getStatus().name() : "");
                row.createCell(9).setCellValue(dto.getMemo() != null ? dto.getMemo() : "");
            }
            workbook.write(os);
        }
    }
}
