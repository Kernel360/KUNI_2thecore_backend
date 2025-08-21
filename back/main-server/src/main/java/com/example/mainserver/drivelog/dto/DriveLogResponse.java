package com.example.mainserver.drivelog.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.bind.annotation.BindParam;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// 조회 응답용
@Builder
@Getter
@AllArgsConstructor
public class DriveLogResponse {
    private Long driveLogId;
    private Long carId;
    private String startPoint;
    private String startLatitude;
    private String startLongitude;
    private LocalDateTime startTime;
    private String endPoint;
    private String endLatitude;
    private String endLongitude;
    private LocalDateTime endTime;
    private BigDecimal driveDist;
    private LocalDateTime createdAt;
}
