package com.example.emulatorserver.device.controller.dto;

import com.example.common.domain.emulator.EmulatorStatus;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetEmulatorResponseData {
    private String deviceId;
    private String carNumber;
    private EmulatorStatus emulatorStatus;
}
