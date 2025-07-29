package com.example._thecore_back.drivelog.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// 등록/수정용
@Getter
@Setter
public class DriveLogRequest {

    private BigDecimal driveDist;
    private String speed;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long carId;
    private Long locationId;
    // createdAt은 요청하는 값이 아니므로 제외
}
