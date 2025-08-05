package com.example.emulatorserver.device.application;

import com.example.common.domain.car.CarEntity;
import com.example.common.domain.car.CarReader;
import com.example.common.domain.emulator.EmulatorEntity;
import com.example.emulatorserver.device.controller.dto.LogPowerDto;
import com.example.common.domain.emulator.EmulatorReader;
import com.example.common.domain.emulator.EmulatorStatus;
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
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LogServiceTest {

    @Mock
    private CarReader carReader;

    @Mock
    private EmulatorReader emulatorReader;

    @InjectMocks
    private LogService logService;

    @Mock
    private GpxScheduler gpxScheduler;

    @Test
    @DisplayName("changePowerStatus - ON 전송 시 성공")
    void changePowerStatusSuccess() {
        // given
        LogPowerDto input = LogPowerDto.builder()
                .carNumber("12가3456")
                .loginId("Test")
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
        verify(gpxScheduler).setCarNumber("12가3456");
        verify(gpxScheduler).setLoginId("Test");
        verify(gpxScheduler).init();
        verify(gpxScheduler).startScheduler();
    }

    @Test
    @DisplayName("changePowerStatus - OFF 전송 시 성공")
    void changePowerStatus_off_shouldStopScheduler() {
        // given
        LogPowerDto input = LogPowerDto.builder()
                .carNumber("12가3456")
                .loginId("Test")
                .powerStatus("OFF")
                .build();

        CarEntity car = CarEntity.builder()
                .carNumber("12가3456")
                .emulatorId(1)
                .build();

        EmulatorEntity emulator = EmulatorEntity.builder()
                .carNumber("12가3456")
                .status(EmulatorStatus.ON)
                .build();

        when(carReader.findByCarNumber("12가3456")).thenReturn(Optional.of(car));
        when(emulatorReader.getById(1)).thenReturn(emulator);

        // when
        LogPowerDto result = logService.changePowerStatus(input);

        // then
        assertNotNull(result);
        assertEquals("OFF", result.getPowerStatus());
        verify(gpxScheduler).stopScheduler();
    }

    @Test
    @DisplayName("changePowerStatus - fail: 존재하지 않는 차량")
    void changePowerStatusFail_carNotFound() {
        // given
        LogPowerDto input = LogPowerDto.builder()
                .carNumber("99가9999")
                .loginId("Test")
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
                .loginId("Test")
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