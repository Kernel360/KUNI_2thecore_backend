package com.example.emulatorserver.device.application.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Builder
@Getter
@Setter
@ToString
public class GpxRequestDto {
    private String carNumber;
    private String loginId;
    private String startTime;
    private String endTime;
    private List<GpxLogDto> locations;
}
