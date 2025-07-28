package com.example.emulatorserver.emulator.application;

import com.example.emulatorserver.device.application.LogService;
import com.example.emulatorserver.device.controller.dto.LogPowerDto;
import com.example.emulatorserver.device.domain.car.CarEntity;
import com.example.emulatorserver.device.domain.car.CarReader;
import com.example.emulatorserver.device.domain.emulator.EmulatorEntity;
import com.example.emulatorserver.device.domain.emulator.EmulatorReader;
import com.example.emulatorserver.device.domain.emulator.EmulatorStatus;
import com.example.emulatorserver.device.exception.car.CarNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LogServiceTest {

    @Mock
    private CarReader carReader;

    @Mock
    private EmulatorReader emulatorReader;

    @InjectMocks
    private LogService logService;

    @Test
    @DisplayName("changePowerStatus - 성공")
    void changePowerStatusSuccess() {
        // given
        LogPowerDto input = LogPowerDto.builder()
                .carNumber("12가3456")
                .powerStatus("ON")
                .build();

        CarEntity car = CarEntity.builder()
                .carNumber("12가3456")
                .emulatorId(1)
                .build();

        EmulatorEntity emulator = EmulatorEntity.builder()
                .carNumber("12가3456")
                .status(EmulatorStatus.OFF)
                .build();

        when(carReader.findByCarNumber("12가3456")).thenReturn(Optional.of(car));
        when(emulatorReader.getById(1)).thenReturn(emulator);

        // when
        LogPowerDto result = logService.changePowerStatus(input);

        // then
        assertNotNull(result);
        assertEquals("12가3456", result.getCarNumber());
        assertEquals("ON", result.getPowerStatus());
    }

    @Test
    @DisplayName("changePowerStatus - fail: 존재하지 않는 차량")
    void changePowerStatusFail_carNotFound() {
        // given
        LogPowerDto input = LogPowerDto.builder()
                .carNumber("99가9999")
                .powerStatus("OFF")
                .build();

        when(carReader.findByCarNumber("99가9999")).thenReturn(Optional.empty());

        // when & then
        assertThrows(CarNotFoundException.class, () -> {
            logService.changePowerStatus(input);
        });
    }

    @Test
    @DisplayName("changePowerStatus - fail: 존재하지 않는 powerStatus 값")
    void changePowerStatusFail_invalidStatus() {
        // given
        LogPowerDto input = LogPowerDto.builder()
                .carNumber("12가3456")
                .powerStatus("INVALID")
                .build();

        CarEntity car = CarEntity.builder()
                .carNumber("12가3456")
                .emulatorId(1)
                .build();

        EmulatorEntity emulator = EmulatorEntity.builder()
                .carNumber("12가3456")
                .status(EmulatorStatus.OFF)
                .build();

        when(carReader.findByCarNumber("12가3456")).thenReturn(Optional.of(car));
        when(emulatorReader.getById(1)).thenReturn(emulator);

        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            logService.changePowerStatus(input);
        });
    }
}