package com.example.emulatorserver.device.controller.dto;

import com.example.emulatorserver.device.domain.emulator.EmulatorStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetEmulatorResponseData {
    private Long emulatorId;
    private String carNumber;
    private EmulatorStatus emulatorStatus;
}
