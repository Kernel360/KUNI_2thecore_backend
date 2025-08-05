package com.example.mainserver.drivelog.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// 등록/수정용
@Builder
@Getter
@Setter
public class DriveLogRequest {

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
    private String speed;
    // createdAt은 요청하는 값이 아니므로 제외
}
