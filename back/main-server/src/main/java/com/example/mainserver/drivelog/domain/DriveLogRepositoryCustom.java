package com.example.mainserver.drivelog.domain;

import java.time.LocalDateTime;
import java.util.List;

public interface DriveLogRepositoryCustom {
    List<DriveLog> searchByConditions(Long carId, LocalDateTime startTime, LocalDateTime endTime);
}
