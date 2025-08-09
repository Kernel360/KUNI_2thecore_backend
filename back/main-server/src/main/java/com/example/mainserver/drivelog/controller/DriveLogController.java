package com.example.mainserver.drivelog.controller;

import com.example.common.dto.ApiResponse;
import com.example.mainserver.drivelog.application.DriveLogService;
import com.example.mainserver.drivelog.domain.DriveLog;
import com.example.mainserver.drivelog.dto.DriveLogFilterRequestDto;
import com.example.mainserver.drivelog.dto.DriveLogFilterResponseDto;
import com.example.mainserver.drivelog.dto.DriveLogRequest;
import com.example.mainserver.drivelog.dto.DriveLogResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/drivelogs")
public class DriveLogController {

    private final DriveLogService driveLogService;

    @PostMapping
    public ResponseEntity<ApiResponse<DriveLogResponse>> createDriveLog(@RequestBody DriveLogRequest request) {
        DriveLog saved = driveLogService.save(request);
        return ResponseEntity.ok(ApiResponse.success("주행기록 저장 완료", toResponse(saved)));
    }

//    @GetMapping
//    public ResponseEntity<ApiResponse<List<DriveLogResponse>>> getAllLogs() {
//        List<DriveLogResponse> responses = driveLogService.getAllLogs().stream()
//                .map(this::toResponse)
//                .collect(Collectors.toList());
//        return ResponseEntity.ok(ApiResponse.success("전체 주행기록 조회 완료", responses));
//    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<DriveLogResponse>> getLogById(@PathVariable Long id) {
        DriveLog driveLog = driveLogService.getLogById(id);
        return ResponseEntity.ok(ApiResponse.success("ID로 조회 성공", toResponse(driveLog)));
    }

    @GetMapping("/car/{carId}")
    public ResponseEntity<ApiResponse<List<DriveLogResponse>>> getLogsByCarId(@PathVariable Long carId) {
        List<DriveLogResponse> responses = driveLogService.getLogsByCarId(carId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success("차량별 주행기록 조회 완료", responses));
    }

    @GetMapping("/range")
    public ResponseEntity<ApiResponse<List<DriveLogResponse>>> getLogsBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        List<DriveLogResponse> responses = driveLogService.getLogsBetween(start, end).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(ApiResponse.success("기간별 주행기록 조회 완료", responses));
    }


    @GetMapping()
    public ApiResponse<Page<DriveLogFilterResponseDto>> getLogsByFilter(
            @Validated @ModelAttribute DriveLogFilterRequestDto driveLogFilterRequestDto,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int offset
    ){

        log.info("Request DTO: {}", driveLogFilterRequestDto);
        var response = driveLogService.getDriveLogByFilter(
                driveLogFilterRequestDto,
                page,
                offset);


        return ApiResponse.success(response);
    }



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
