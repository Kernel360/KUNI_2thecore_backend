package com.example._thecore_back.emulator.controller.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FinalDeleteEmulatorResponse {
    private String message;
    private DeleteEmulatorResponseData data;
}
