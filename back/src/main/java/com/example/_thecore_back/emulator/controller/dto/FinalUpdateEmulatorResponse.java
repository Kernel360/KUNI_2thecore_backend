package com.example._thecore_back.emulator.controller.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FinalUpdateEmulatorResponse {
    private String message;
    private UpdateEmulatorResponseData data;
}
