package com.example.mainserver.drivelog.dto;


import com.example.common.domain.car.CarStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DriveLogFilterResponseDto {

    private String carNumber;

    private String brand;

    private String model;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;


    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;


    private String startPoint;

    private String endPoint;

    private double driveDist;

    private CarStatus status;

    private String memo;
}
