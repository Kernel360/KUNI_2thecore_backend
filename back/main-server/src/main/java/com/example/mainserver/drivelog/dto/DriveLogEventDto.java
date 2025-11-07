package com.example.mainserver.drivelog.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DriveLogEventDto {

    private String carNumber;

    private String status;

    private LocalDateTime eventTime;

}
