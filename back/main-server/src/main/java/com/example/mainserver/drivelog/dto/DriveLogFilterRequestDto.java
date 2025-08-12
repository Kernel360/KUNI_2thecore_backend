package com.example.mainserver.drivelog.dto;


import com.example.common.domain.car.CarStatus;
import com.example.mainserver.drivelog.dto.validator.DriveLogDateRangeCheck;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
@DriveLogDateRangeCheck
public class DriveLogFilterRequestDto {

    private String carNumber;

    private CarStatus status;

    private String brand;

    private String model;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startTime;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endTime;

    @NotNull
    private boolean twoParam;


}
