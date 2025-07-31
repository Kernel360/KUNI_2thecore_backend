package com.example.emulatorserver.device.controller.dto;

import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EmulatorRequest {

    @NotNull(message = "차량 번호는 필수입니다.")
    private String carNumber;
}
