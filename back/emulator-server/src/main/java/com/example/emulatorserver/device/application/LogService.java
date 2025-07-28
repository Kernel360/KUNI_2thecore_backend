package com.example.emulatorserver.device.application;
import com.example.emulatorserver.device.domain.emulator.EmulatorEntity;
import com.example.emulatorserver.device.domain.emulator.EmulatorReader;
import com.example.emulatorserver.device.domain.emulator.EmulatorStatus;
import com.example.emulatorserver.device.controller.dto.LogPowerDto;
import com.example.emulatorserver.device.domain.car.CarEntity;
import com.example.emulatorserver.device.domain.car.CarReader;
import com.example.emulatorserver.device.exception.car.CarErrorCode;
import com.example.emulatorserver.device.exception.car.CarNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogService {

    private final CarReader carReader;
    private final EmulatorReader emulatorReader;

    public LogPowerDto changePowerStatus(LogPowerDto logPowerDto){
        String carNumber = logPowerDto.getCarNumber();
        String powerStatus = logPowerDto.getPowerStatus();

        CarEntity carEntity = carReader.findByCarNumber(carNumber)
                .orElseThrow(() -> new CarNotFoundException(CarErrorCode.CAR_NOT_FOUND_BY_NUMBER, carNumber));

        EmulatorEntity emulatorEntity = emulatorReader.getById(carEntity.getEmulatorId());

        emulatorEntity.setStatus(EmulatorStatus.getEmulatorStatus(powerStatus));

        return LogPowerDto.builder()
                .carNumber(emulatorEntity.getCarNumber())
                .powerStatus(emulatorEntity.getStatus().toString())
                .build();
    }
}
