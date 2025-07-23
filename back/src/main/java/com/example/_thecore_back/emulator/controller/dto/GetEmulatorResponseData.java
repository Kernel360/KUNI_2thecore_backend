package com.example._thecore_back.emulator.controller.dto;

import com.example._thecore_back.emulator.domain.EmulatorStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GetEmulatorResponseData {
    private int emulatorId;
    private String carNumber;
    private EmulatorStatus emulatorStatus;
}
