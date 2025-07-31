package com.example.emulatorserver.device.controller.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy.class)
public class LogPowerDto {
    @NotBlank
    private String carNumber;

    @NotBlank
    private String loginId;

    @NotBlank
    private String powerStatus;
}
