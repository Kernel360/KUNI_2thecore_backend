package com.example.emulatorserver.device.controller.dto;

import com.example.emulatorserver.device.domain.emulator.EmulatorStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RegisterEmulatorResponseData {
    private Long emulatorId;
    private String carNumber;
    private EmulatorStatus emulatorStatus;
}
