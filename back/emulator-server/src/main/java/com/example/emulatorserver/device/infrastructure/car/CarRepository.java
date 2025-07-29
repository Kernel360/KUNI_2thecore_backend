package com.example.emulatorserver.device.infrastructure.car;

import com.example.emulatorserver.device.domain.car.CarEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarRepository extends JpaRepository<CarEntity, Integer> {

    Optional<CarEntity> findByCarNumber(String carNumber);

    Optional<CarEntity> findByEmulatorId(Integer emulatorId);

}
