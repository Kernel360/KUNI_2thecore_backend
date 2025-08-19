package com.example.mainserver.collector.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class GpsLogResponseDto {

    @NotNull
    private String carNumber;

}
