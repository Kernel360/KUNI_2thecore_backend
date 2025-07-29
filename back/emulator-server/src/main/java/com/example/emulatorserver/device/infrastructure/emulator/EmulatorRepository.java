package com.example.emulatorserver.device.infrastructure.emulator;

import com.example.emulatorserver.device.domain.emulator.EmulatorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmulatorRepository extends JpaRepository<EmulatorEntity, Integer> {
    Optional<EmulatorEntity> findByDeviceId(String deviceId);
    void deleteByDeviceId(String deviceId);
}
