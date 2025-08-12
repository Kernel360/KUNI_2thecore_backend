package com.example.emulatorserver.device.application;
import com.example.common.domain.car.CarEntity;
import com.example.common.domain.car.CarReader;
import com.example.common.domain.emulator.EmulatorEntity;
import com.example.common.domain.emulator.EmulatorReader;
import com.example.common.domain.emulator.EmulatorStatus;
import com.example.emulatorserver.device.controller.dto.LogPowerDto;
import com.example.emulatorserver.device.exception.car.CarErrorCode;
import com.example.emulatorserver.device.exception.car.CarNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogService {

    private final CarReader carReader;
    private final EmulatorReader emulatorReader;
    private final GpxScheduler gpxScheduler;

    public LogPowerDto changePowerStatus(LogPowerDto logPowerDto) {
        String carNumber = logPowerDto.getCarNumber();
        String loginId = logPowerDto.getLoginId();
        String powerStatus = logPowerDto.getPowerStatus();

        CarEntity carEntity = carReader.findByCarNumber(carNumber)
                .orElseThrow(() -> new CarNotFoundException(CarErrorCode.CAR_NOT_FOUND_BY_NUMBER, carNumber));

<<<<<<< HEAD
        EmulatorEntity emulatorEntity = emulatorReader.getById(carEntity.getEmulatorId());

        emulatorEntity.setStatus(EmulatorStatus.getEmulatorStatus(powerStatus));

=======
>>>>>>> 144bb01 ("emulator-code-delete")
        if(powerStatus.equals("ON")) {
            // scheduler 시작
            gpxScheduler.setCarNumber(carNumber);
            gpxScheduler.setLoginId(loginId);

            gpxScheduler.init();
            gpxScheduler.startScheduler();
        }

        if(powerStatus.equals("OFF")) {
            gpxScheduler.stopScheduler();
        }

        return LogPowerDto.builder()
                .carNumber(emulatorEntity.getCarNumber())
                .loginId(loginId)
                .powerStatus(emulatorEntity.getStatus().toString())
                .build();
    }
}
