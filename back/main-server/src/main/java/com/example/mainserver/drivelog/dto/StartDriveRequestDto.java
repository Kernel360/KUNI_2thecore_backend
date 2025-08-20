package com.example.mainserver.drivelog.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StartDriveRequestDto {
    private String carNumber;
    private String startLatitude;
    private String startLongitude;
    private LocalDateTime startTime;

}
