package com.example.emulatorserver.device.infrastructure.emulator;

import com.example.emulatorserver.device.domain.emulator.EmulatorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmulatorRepository extends JpaRepository<EmulatorEntity, Long> {
    // TODO: car_number -> device_id 임시 변경. 추후 car테이블 머지 후 수정 필요
    Optional<EmulatorEntity> findByDeviceId(String deviceId);

}
