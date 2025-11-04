package com.example.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class LiveLocationDto {
    // 웹소켓 전달용
    private String carNumber;
    private String latitude;
    private String longitude;
}
