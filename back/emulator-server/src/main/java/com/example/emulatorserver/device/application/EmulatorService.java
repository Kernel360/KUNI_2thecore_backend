package com.example.emulatorserver.device.application;

import com.example.emulatorserver.device.domain.car.CarEntity;
import com.example.emulatorserver.device.domain.emulator.EmulatorEntity;
import com.example.emulatorserver.device.domain.emulator.EmulatorStatus;
import com.example.emulatorserver.device.controller.dto.EmulatorRequest;
import com.example.emulatorserver.device.infrastructure.car.CarRepository;
import com.example.emulatorserver.device.infrastructure.emulator.EmulatorRepository;
import com.example.emulatorserver.device.exception.emulator.CarNotFoundException;
import com.example.emulatorserver.device.exception.emulator.EmulatorNotFoundException;
import com.example.emulatorserver.device.exception.emulator.DuplicateEmulatorException;

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

        carEntity.setEmulatorId(savedEntity.getId().intValue());
        carRepository.save(carEntity);

        return savedEntity;
    }

    public EmulatorEntity getEmulator(Long id) {
        return emulatorRepository.findById(id)
                .orElseThrow(() -> new EmulatorNotFoundException("해당하는 애뮬레이터가 없습니다: " + id));
    }

    public List<EmulatorEntity> getAllEmulators() {
        return emulatorRepository.findAll();
    }

    @Transactional
    public EmulatorEntity updateEmulator(Long id, EmulatorRequest emulatorRequest) {
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

            newCarEntity.setEmulatorId(entity.getId().intValue());
            carRepository.save(newCarEntity);

            entity.setDeviceId(newDeviceId);
        }

        return emulatorRepository.save(entity);
    }

    @Transactional
    public void deleteEmulator(Long id) {
        EmulatorEntity emulatorEntity = emulatorRepository.findById(id)
                .orElseThrow(() -> new EmulatorNotFoundException("해당하는 애뮬레이터가 없습니다: " + id));

        carRepository.findByCarNumber(emulatorEntity.getDeviceId()).ifPresent(carEntity -> {
            carEntity.setEmulatorId(null);
            carRepository.save(carEntity);
        });

        emulatorRepository.deleteById(id);
    }
}