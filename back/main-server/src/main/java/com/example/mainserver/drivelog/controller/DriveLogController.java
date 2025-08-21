package com.example.mainserver.drivelog.controller;

import com.example.common.dto.ApiResponse;
import com.example.mainserver.drivelog.application.DriveLogService;
import com.example.mainserver.drivelog.domain.DriveLog;
import com.example.mainserver.drivelog.dto.*;
import com.example.mainserver.drivelog.dto.LocationUpdateRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletResponse;
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

    @PostMapping("/start")// 주행 시작 후 end정보 비어있는 주행 기록 생성하는 API
    public ResponseEntity<ApiResponse<DriveLogResponse>> startDrive(@RequestBody StartDriveRequestDto request) {
        DriveLog saved = driveLogService.startDrive(request);
        return ResponseEntity.ok(ApiResponse.success("주행기록 생성 완료", toResponse(saved)));
    }

    @PostMapping("/end")// 주행 종료 후 주행 기록의 비어있는 필드 채우는 API
    public ResponseEntity<ApiResponse<DriveLogResponse>> endDrive(@RequestBody EndDriveRequestDto request) {
        DriveLog saved = driveLogService.endDrive(request);
        return ResponseEntity.ok(ApiResponse.success("주행기록 수정 완료", toResponse(saved)));
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

    @PostMapping("/update-location")
    public ResponseEntity<ApiResponse<String>> updateRealTimeLocation(@RequestBody LocationUpdateRequest request) {
        try {
            // 현재 진행 중인 드라이브 로그를 찾아서 업데이트
            DriveLog updatedLog = driveLogService.updateCurrentDriveLogLocation(
                    request.getCarId(), 
                    request.getNewLatitude(), 
                    request.getNewLongitude()
            );
            
            if (updatedLog != null) {
                return ResponseEntity.ok(ApiResponse.success("실시간 위치 업데이트 완료"));
            } else {
                return ResponseEntity.ok(ApiResponse.success("진행 중인 드라이브 로그 없음"));
            }
        } catch (Exception e) {
            log.error("실시간 위치 업데이트 실패: {}", e.getMessage());
            return ResponseEntity.ok(ApiResponse.success("업데이트 실패: " + e.getMessage()));
        }
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
                .createdAt(log.getCreatedAt())
                .build();
    }
    @GetMapping("/excel")
    public void downloadDriveLogsExcel(
            DriveLogFilterRequestDto filterDto, // 프론트와 동일한 방식의 요청 파라미터
            HttpServletResponse response) throws Exception {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=drive_logs.xlsx");

        // 원하는 조건의 리스트 조회. (페이징 없이 전체, 혹은 화면 리스트)
        List<DriveLogFilterResponseDto> dtos = driveLogService.getFilteredDriveLogs(filterDto);
        driveLogService.writeDriveLogsToExcel(response.getOutputStream(), dtos);
    }
}
