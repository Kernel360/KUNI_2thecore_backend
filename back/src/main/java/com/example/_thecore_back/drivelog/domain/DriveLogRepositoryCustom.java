package com.example._thecore_back.drivelog.domain;


import java.time.LocalDateTime;
import java.util.List;

public interface DriveLogRepositoryCustom {
    List<DriveLog> searchByConditions(Long carId, Long locationId, LocalDateTime startTime, LocalDateTime endTime);
}
