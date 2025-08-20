package com.example.mainserver.drivelog.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DriveLogRepository extends JpaRepository<DriveLog, Long> {

    // 특정 차량의 주행 기록 조회
    List<DriveLog> findByCarId(Long carId);
    // 특정 기간 사이의 주행 기록 조회
    List<DriveLog> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);

    DriveLog getByCarId(Long carId);

    DriveLog findByCarIdAndStartTime(Long carId, LocalDateTime startTime);}
