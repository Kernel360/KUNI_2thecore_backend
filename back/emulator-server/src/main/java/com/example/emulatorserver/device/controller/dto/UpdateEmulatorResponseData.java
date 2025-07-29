package com.example.emulatorserver.device.controller.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateEmulatorResponseData {
    private String deviceId;
    private String carNumber;
}
