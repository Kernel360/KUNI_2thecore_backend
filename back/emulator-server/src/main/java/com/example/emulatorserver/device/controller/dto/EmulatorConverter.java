package com.example.emulatorserver.device.controller.dto;

import com.example.emulatorserver.device.domain.emulator.EmulatorEntity;
import org.springframework.stereotype.Component;

@Component
public class EmulatorConverter {

    // 애뮬레이터 등록
    public RegisterEmulatorResponseData toRegisterEmulatorData(
            EmulatorEntity emulatorEntity
    ) {
        return RegisterEmulatorResponseData.builder()
                .deviceId(emulatorEntity.getDeviceId())
                .carNumber(emulatorEntity.getCarNumber())
                .emulatorStatus(emulatorEntity.getStatus())
                .build()
                ;
    }

    // 애뮬레이터 상세 조회
    public GetEmulatorResponseData toGetEmulatorData(
            EmulatorEntity emulatorEntity
    ) {
        return GetEmulatorResponseData.builder()
                .deviceId(emulatorEntity.getDeviceId())
                .carNumber(emulatorEntity.getCarNumber())
                .emulatorStatus(emulatorEntity.getStatus())
                .build()
                ;
    }

    // 애뮬레이터 수정
    public UpdateEmulatorResponseData toUpdateEmulatorData(
            EmulatorEntity emulatorEntity
    ) {
        return UpdateEmulatorResponseData.builder()
                .deviceId(emulatorEntity.getDeviceId())
                .carNumber(emulatorEntity.getCarNumber())
                .build()
                ;
    }
}
