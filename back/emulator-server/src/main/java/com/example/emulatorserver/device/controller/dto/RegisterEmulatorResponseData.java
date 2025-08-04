package com.example.emulatorserver.device.controller.dto;

import com.example.common.domain.emulator.EmulatorStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class RegisterEmulatorResponseData {
    private String deviceId;
    private String carNumber;
    private EmulatorStatus emulatorStatus;
}
