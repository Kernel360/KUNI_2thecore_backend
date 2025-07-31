package com.example.emulatorserver.device.application.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Builder
@Getter
@Setter
@ToString
public class GpxLogDto {
    private String timeStamp;
    private String latitude; // 위도
    private String longitude; // 경도
}
