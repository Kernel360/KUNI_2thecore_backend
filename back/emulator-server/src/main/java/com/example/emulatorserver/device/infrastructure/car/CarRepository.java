package com.example.emulatorserver.device.infrastructure.car;

import com.example.emulatorserver.device.domain.car.CarEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository("emulatorServerCarRepository")
public interface CarRepository extends JpaRepository<CarEntity, Integer> {

    Optional<CarEntity> findByCarNumber(String carNumber);

    Optional<CarEntity> findByEmulatorId(Integer emulatorId);

}
