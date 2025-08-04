package com.example._thecore_back.car.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;
import java.util.Optional;

public interface CarReader  {
    Optional<CarEntity> findByCarNumber(String carNumber);

    Page<CarEntity> findAll(Pageable pageable);

    Map<CarStatus, Long> getCountByStatus();

    Optional<CarEntity> findByEmulatorId(Integer emulatorId);

    List<CarEntity> findByStatus(List<CarStatus> statuses);

}