package com.example._thecore_back.emulator.domain;

import java.util.List;
import java.util.Optional;

public interface EmulatorReader {
    Optional<EmulatorEntity> findById(int id);
    List<EmulatorEntity> findAll();

    Optional<EmulatorEntity> findByCarNumber(String carNumber);

}
