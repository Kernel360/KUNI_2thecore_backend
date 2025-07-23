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
        CarEntity carEntity = carRepository.findByCarNumber(emulatorRequest.getDeviceId())
                .orElseThrow(() -> new CarNotFoundException("해당하는 차량이 존재하지 않습니다: " + emulatorRequest.getDeviceId()));

        if (carEntity.getEmulatorId() != null) {
            throw new DuplicateEmulatorException("해당 차량에 이미 애뮬레이터가 연결되어 있습니다: " + emulatorRequest.getDeviceId());
        }

        emulatorRepository.findByDeviceId(emulatorRequest.getDeviceId()).ifPresent(emulator -> {
            throw new DuplicateEmulatorException("이미 등록된 애뮬레이터입니다: " + emulatorRequest.getDeviceId());
        });

        EmulatorEntity entity = EmulatorEntity.builder()
                .deviceId(emulatorRequest.getDeviceId())
                .status(EmulatorStatus.OFF)
                .build();

        EmulatorEntity savedEntity = emulatorRepository.save(entity);

        carEntity.setEmulatorId(savedEntity.getId());
        carRepository.save(carEntity);

        return savedEntity;
    }

    public EmulatorEntity getEmulator(int id) {
        return emulatorRepository.findById(id)
                .orElseThrow(() -> new EmulatorNotFoundException("해당하는 애뮬레이터가 없습니다: " + id));
    }

    public List<EmulatorEntity> getAllEmulators() {
        return emulatorRepository.findAll();
    }

    @Transactional
    public EmulatorEntity updateEmulator(int id, EmulatorRequest emulatorRequest) {
        EmulatorEntity entity = emulatorRepository.findById(id)
                .orElseThrow(() -> new EmulatorNotFoundException("해당하는 애뮬레이터가 없습니다.: " + id));

        String currentDeviceId = entity.getDeviceId();
        String newDeviceId = emulatorRequest.getDeviceId();

        if (!Objects.equals(currentDeviceId, newDeviceId)) {
            CarEntity newCarEntity = carRepository.findByCarNumber(newDeviceId)
                    .orElseThrow(() -> new CarNotFoundException("해당하는 차량이 존재하지 않습니다: " + newDeviceId));

            if (newCarEntity.getEmulatorId() != null) {
                throw new DuplicateEmulatorException("해당 차량에 이미 애뮬레이터가 연결되어 있습니다: " + newDeviceId);
            }

            emulatorRepository.findByDeviceId(newDeviceId).ifPresent(emulator -> {
                throw new DuplicateEmulatorException("이미 등록된 애뮬레이터입니다: " + newDeviceId);
            });

            carRepository.findByCarNumber(currentDeviceId).ifPresent(oldCar -> {
                oldCar.setEmulatorId(null);
                carRepository.save(oldCar);
            });

            newCarEntity.setEmulatorId(entity.getId());
            carRepository.save(newCarEntity);

            entity.setDeviceId(newDeviceId);
        }

        return emulatorRepository.save(entity);
    }

    @Transactional
    public void deleteEmulator(int id) {
        EmulatorEntity emulatorEntity = emulatorRepository.findById(id)
                .orElseThrow(() -> new EmulatorNotFoundException("해당하는 애뮬레이터가 없습니다: " + id));

        carRepository.findByCarNumber(emulatorEntity.getDeviceId()).ifPresent(carEntity -> {
            carEntity.setEmulatorId(null);
            carRepository.save(carEntity);
        });

        emulatorRepository.deleteById(id);
    }
}