package com.example.emulatorserver.device.application;

import com.example.emulatorserver.device.domain.car.CarEntity;
import com.example.emulatorserver.device.domain.emulator.EmulatorEntity;
import com.example.emulatorserver.device.domain.emulator.EmulatorStatus;
import com.example.emulatorserver.device.controller.dto.EmulatorRequest;
import com.example.emulatorserver.device.exception.car.CarErrorCode;
import com.example.emulatorserver.device.exception.car.CarNotFoundException;
import com.example.emulatorserver.device.exception.emulator.EmulatorErrorCode;
import com.example.emulatorserver.device.infrastructure.car.CarRepository;
import com.example.emulatorserver.device.infrastructure.emulator.EmulatorRepository;
import com.example.emulatorserver.device.exception.emulator.EmulatorNotFoundException;
import com.example.emulatorserver.device.exception.emulator.DuplicateEmulatorException;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EmulatorService {

    private final EmulatorRepository emulatorRepository;
    private final CarRepository carRepository;

    @Transactional
    public EmulatorEntity registerEmulator(EmulatorRequest emulatorRequest) {
        String carNumber = emulatorRequest.getCarNumber();

        // 존재하는 차량인지 확인
        CarEntity carEntity = carRepository.findByCarNumber(carNumber)
                .orElseThrow(() -> new CarNotFoundException
                        (CarErrorCode.CAR_NOT_FOUND_BY_NUMBER, carNumber));

        // 해당 차량에 이미 애뮬레이터가 연결되어 있는지 확인
        if (carEntity.getEmulatorId() != null) {
            throw new DuplicateEmulatorException(EmulatorErrorCode.DUPLICATE_EMULATOR, carNumber);
        }

        EmulatorEntity entity = EmulatorEntity.builder()
                .carNumber(carNumber)
                .status(EmulatorStatus.OFF)
                .build();

        EmulatorEntity savedEntity = emulatorRepository.save(entity);

        carEntity.setEmulatorId(savedEntity.getId());
        carRepository.save(carEntity);

        savedEntity.setCarNumber(carNumber);
        return savedEntity;
    }

    public EmulatorEntity getEmulator(String deviceId) {
        EmulatorEntity emulatorEntity = emulatorRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new EmulatorNotFoundException(EmulatorErrorCode.EMULATOR_NOT_FOUND, deviceId));

        // carRepository를 사용해 carNumber를 찾아와서 Transient 필드에 설정
        carRepository.findByEmulatorId(emulatorEntity.getId()).ifPresent(car -> {
            emulatorEntity.setCarNumber(car.getCarNumber());
        });

        return emulatorEntity;
    }

    public Page<EmulatorEntity> getAllEmulators(Pageable pageable) {
        Page<EmulatorEntity> emulatorsPage = emulatorRepository.findAll(pageable);
        emulatorsPage.getContent().forEach(emulator -> {
            carRepository.findByEmulatorId(emulator.getId()).ifPresent(car -> {
                emulator.setCarNumber(car.getCarNumber());
            });
        });
        return emulatorsPage;
    }

    @Transactional
    public EmulatorEntity updateEmulator(String deviceId, EmulatorRequest emulatorRequest) {
        EmulatorEntity entity = emulatorRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new EmulatorNotFoundException(EmulatorErrorCode.EMULATOR_NOT_FOUND, deviceId));

        String newCarNumber = emulatorRequest.getCarNumber();

        // 기존 차량 연결 해제
        carRepository.findByEmulatorId(entity.getId()).ifPresent(oldCar -> {
            if(!Objects.equals(oldCar.getCarNumber(), newCarNumber)) {
                oldCar.setEmulatorId(null);
                carRepository.save(oldCar);
            }
        });

        // 새로운 차량 찾기 및 연결
        CarEntity newCarEntity = carRepository.findByCarNumber(newCarNumber)
                .orElseThrow(() -> new CarNotFoundException(CarErrorCode.CAR_NOT_FOUND_BY_NUMBER, newCarNumber));

        newCarEntity.setEmulatorId(entity.getId());
        carRepository.save(newCarEntity);

        // @Transient 필드 채워서 return
        entity.setCarNumber(newCarNumber);
        return entity;
    }

    @Transactional
    public void deleteEmulator(String deviceId) {
        EmulatorEntity emulatorEntity = emulatorRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new EmulatorNotFoundException(EmulatorErrorCode.EMULATOR_NOT_FOUND, deviceId));

        carRepository.findByEmulatorId(emulatorEntity.getId()).ifPresent(carEntity -> {
            carEntity.setEmulatorId(null);
            carRepository.save(carEntity);
        });

        emulatorRepository.delete(emulatorEntity);
    }
}