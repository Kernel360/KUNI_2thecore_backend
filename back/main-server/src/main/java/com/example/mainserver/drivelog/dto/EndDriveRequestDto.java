package com.example.mainserver.drivelog.dto;

import com.example.mainserver.drivelog.dto.validator.DriveLogDateRangeCheck;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EndDriveRequestDto {
    // 주행기록 찾는 키로 사용 - num, startTime
    private String carNumber;
    private LocalDateTime startTime;

    private String endLatitude;
    private String endLongitude;
    private LocalDateTime endTime;
}
