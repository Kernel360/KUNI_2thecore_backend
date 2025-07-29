package com.example._thecore_back.drivelog.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// 조회 응답용
@Getter
@AllArgsConstructor
public class DriveLogResponse {
    private Long id;
    private BigDecimal driveDist;
    private String speed;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createdAt;
    private Long carId;
    private Long locationId;
}
