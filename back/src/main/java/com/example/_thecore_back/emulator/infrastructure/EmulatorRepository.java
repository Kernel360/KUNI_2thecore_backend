package com.example._thecore_back.emulator.infrastructure;

import com.example._thecore_back.emulator.domain.EmulatorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmulatorRepository extends JpaRepository<EmulatorEntity, Integer> {
    Optional<EmulatorEntity> findByDeviceId(String deviceId);
    void deleteByDeviceId(String deviceId);
}
