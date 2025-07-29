package com.example._thecore_back.emulator.infrastructure;

import com.example._thecore_back.emulator.domain.EmulatorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmulatorRepository extends JpaRepository<EmulatorEntity, Integer> {
    Optional<EmulatorEntity> findByDeviceId(String deviceId);
    Optional<EmulatorEntity> findByCarNumber(String carNumber);
    void deleteByDeviceId(String deviceId);
}
