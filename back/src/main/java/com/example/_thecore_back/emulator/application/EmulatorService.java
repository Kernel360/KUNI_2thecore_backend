package com.example._thecore_back.emulator.application;

import com.example._thecore_back.car.domain.CarEntity;
import com.example._thecore_back.car.infrastructure.CarRepository;
import com.example._thecore_back.emulator.domain.EmulatorEntity;
import com.example._thecore_back.emulator.domain.EmulatorStatus;
import com.example._thecore_back.emulator.controller.dto.EmulatorRequest;
import com.example._thecore_back.emulator.infrastructure.EmulatorRepository;
import com.example._thecore_back.emulator.exception.CarNotFoundException;
import com.example._thecore_back.emulator.exception.EmulatorNotFoundException;
import com.example._thecore_back.emulator.exception.DuplicateEmulatorException;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class EmulatorService {

    private final EmulatorRepository emulatorRepository;
    private final CarRepository carRepository;

    @Transactional
    public EmulatorEntity registerEmulator(EmulatorRequest emulatorRequest) {
        String carNumber = emulatorRequest.getCarNumber();
        CarEntity carEntity = carRepository.findByCarNumber(carNumber)
                .orElseThrow(() -> new CarNotFoundException("해당하는 차량이 존재하지 않습니다: " + carNumber));

        if (carEntity.getEmulatorId() != null) {
            throw new DuplicateEmulatorException("해당 차량에 이미 애뮬레이터가 연결되어 있습니다: " + carNumber);
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
                .orElseThrow(() -> new EmulatorNotFoundException("해당하는 애뮬레이터가 없습니다: " + deviceId));

        // carRepository를 사용해 carNumber를 찾아와서 Transient 필드에 설정
        carRepository.findByEmulatorId(emulatorEntity.getId()).ifPresent(car -> {
            emulatorEntity.setCarNumber(car.getCarNumber());
        });

        return emulatorEntity;
    }

    public List<EmulatorEntity> getAllEmulators() {
        List<EmulatorEntity> emulators = emulatorRepository.findAll();
        emulators.forEach(emulator -> {
            carRepository.findByEmulatorId(emulator.getId()).ifPresent(car -> {
                emulator.setCarNumber(car.getCarNumber());
            });
        });
        return emulators;
    }

    @Transactional
    public EmulatorEntity updateEmulator(String deviceId, EmulatorRequest emulatorRequest) {
        EmulatorEntity entity = emulatorRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new EmulatorNotFoundException("해당하는 애뮬레이터가 없습니다.: " + deviceId));

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
                .orElseThrow(() -> new CarNotFoundException("해당하는 차량이 존재하지 않습니다: " + newCarNumber));

        if (newCarEntity.getEmulatorId() != null && newCarEntity.getEmulatorId() != entity.getId()) {
            throw new DuplicateEmulatorException("해당 차량에 이미 다른 애뮬레이터가 연결되어 있습니다: " + newCarNumber);
        }

        newCarEntity.setEmulatorId(entity.getId());
        carRepository.save(newCarEntity);

        // @Transient 필드 채워서 return
        entity.setCarNumber(newCarNumber);
        return entity;
    }

    @Transactional
    public void deleteEmulator(String deviceId) {
        EmulatorEntity emulatorEntity = emulatorRepository.findByDeviceId(deviceId)
                .orElseThrow(() -> new EmulatorNotFoundException("해당하는 애뮬레이터가 없습니다: " + deviceId));

        carRepository.findByEmulatorId(emulatorEntity.getId()).ifPresent(carEntity -> {
            carEntity.setEmulatorId(null);
            carRepository.save(carEntity);
        });

        emulatorRepository.delete(emulatorEntity);
    }
}