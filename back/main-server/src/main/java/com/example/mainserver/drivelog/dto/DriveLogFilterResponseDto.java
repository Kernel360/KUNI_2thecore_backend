package com.example.mainserver.drivelog.dto;


import com.example.common.domain.car.CarStatus;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DriveLogFilterResponseDto {

    private String carNumber;

    private String brand;

    private String model;

    private LocalDate startTime;

    private LocalDate endTime;

    private String startPoint;

    private String endPoint;

    private double driveDist;

    private CarStatus status;
}
