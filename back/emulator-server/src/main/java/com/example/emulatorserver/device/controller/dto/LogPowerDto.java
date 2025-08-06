package com.example.emulatorserver.device.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LogPowerDto {
    @NotBlank
    private String carNumber;

    @NotBlank
    private String loginId;

    @NotBlank
    private String powerStatus;
}
