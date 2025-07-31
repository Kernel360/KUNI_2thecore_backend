package com.example._thecore_back.drivelog.controller;

import com.example._thecore_back.common.dto.ApiResponse;
import com.example._thecore_back.drivelog.application.DriveLogService;
import com.example._thecore_back.drivelog.domain.DriveLog;
import com.example._thecore_back.drivelog.dto.DriveLogRequest;
import com.example._thecore_back.drivelog.dto.DriveLogResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/drivelogs")
public class DriveLogController {

    private final DriveLogService driveLogService;

    // 주행 기록 저장 (POST)
    @PostMapping
    public ResponseEntity<ApiResponse<DriveLogResponse>> createDriveLog(@RequestBody DriveLogRequest request){
        DriveLog saved = driveLogService.save(request);
        return ResponseEntity.ok(ApiResponse.success("주행기록 저장 완료", toResponse(saved)));
    }

    // 전체 주행 기록 조회 (GET)
    @GetMapping
    public ResponseEntity<ApiResponse<List<DriveLogResponse>>> getAllLogs(){
        List<DriveLogResponse> responses = driveLogService.getAllLogs().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success("전체 주행기록 조회 완료", responses));
    }

    // Drive_Log_ID로 단일 주행 기록 조회 (GET)
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DriveLogResponse>> getLogById(@PathVariable Long id){
        DriveLog driveLog = driveLogService.getLogById(id);
        return ResponseEntity.ok(ApiResponse.success("ID로 조회 성공", toResponse(driveLog)));
    }

    // 차량 ID로 주행 기록 리스트 조회 (GET)
    @GetMapping("/car/{carId}")
    public ResponseEntity<ApiResponse<List<DriveLogResponse>>> getLogsByCarId(@PathVariable Long carId) {
        List<DriveLogResponse> responses = driveLogService.getLogsByCarId(carId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success("차량별 주행기록 조회 완료", responses));
    }

    // 특정 기간 사이의 주행 기록 조회 (GET)
    @GetMapping("/range")
    public ResponseEntity<ApiResponse<List<DriveLogResponse>>> getLogsBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end
    ) {
        List<DriveLogResponse> responses = driveLogService.getLogsBetween(start, end).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success("기간별 주행기록 조회 완료", responses));
    }

    // 공통 변환 메서드

    private DriveLogResponse toResponse(DriveLog log) {
        return DriveLogResponse.builder()
                .driveLogId(log.getDriveLogId())
                .carId(log.getCarId())
                .startPoint(log.getStartPoint())
                .startLatitude(log.getStartLatitude())
                .startLongitude(log.getStartLongitude())
                .startTime(log.getStartTime())
                .endPoint(log.getEndPoint())
                .endLatitude(log.getEndLatitude())
                .endLongitude(log.getEndLongitude())
                .endTime(log.getEndTime())
                .driveDist(log.getDriveDist())
                .speed(log.getSpeed())
                .createdAt(log.getCreatedAt())
                .build();
    }
}
