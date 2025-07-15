package com.example._thecore_back.emulator.controller.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class FinalGetAllEmulatorResponse {
    private String message;
    private List<GetEmulatorResponseData> data;
}
