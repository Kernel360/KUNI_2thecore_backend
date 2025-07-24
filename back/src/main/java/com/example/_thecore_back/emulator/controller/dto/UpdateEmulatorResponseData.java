package com.example._thecore_back.emulator.controller.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UpdateEmulatorResponseData {
    private String deviceId;
    private String carNumber;
}
