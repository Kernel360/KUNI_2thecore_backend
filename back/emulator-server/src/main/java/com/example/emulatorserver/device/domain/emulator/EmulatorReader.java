package com.example.emulatorserver.device.domain.emulator;

import java.util.List;
import java.util.Optional;

public interface EmulatorReader {
    Optional<EmulatorEntity> findById(int id);
    List<EmulatorEntity> findAll();
    EmulatorEntity getById(int id);
    Optional<EmulatorEntity> findByCarNumber(String carNumber);

}
