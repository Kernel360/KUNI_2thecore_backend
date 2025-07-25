package com.example.emulatorserver.device.domain.emulator;

import java.util.List;
import java.util.Optional;

public interface EmulatorReader {
    Optional<EmulatorEntity> findById(Long id);
    List<EmulatorEntity> findAll();
    EmulatorEntity getById(Long id);
}
