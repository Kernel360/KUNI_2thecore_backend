package com.example.emulatorserver.device.domain.car;

import java.util.List;
import java.util.Optional;

public interface CarReader  {
    Optional<CarEntity> findByCarNumber(String carNumber);

    List<CarEntity> findAll();

    Optional<CarEntity> findByEmulatorId(Integer emulatorId);
}
