package com.example.common.infrastructure.emulator;


import com.example.common.domain.emulator.EmulatorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmulatorRepository extends JpaRepository<EmulatorEntity, Integer> {
    Optional<EmulatorEntity> findByDeviceId(String deviceId);
    Optional<EmulatorEntity> findByCarNumber(String carNumber);
    void deleteByDeviceId(String deviceId);
}
