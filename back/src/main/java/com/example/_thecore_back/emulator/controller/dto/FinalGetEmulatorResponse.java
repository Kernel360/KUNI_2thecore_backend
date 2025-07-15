package com.example._thecore_back.emulator.controller.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FinalGetEmulatorResponse {
    private String message;
    private GetEmulatorResponseData data;
}
