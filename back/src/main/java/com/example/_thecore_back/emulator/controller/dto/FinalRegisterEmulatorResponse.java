package com.example._thecore_back.emulator.controller.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FinalRegisterEmulatorResponse {
    private String message;
    private RegisterEmulatorResponseData data;
}
