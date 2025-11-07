package com.example.mainserver.drivelog.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DriveLogRepository extends JpaRepository<DriveLog, Long> {

    // 특정 차량의 주행 기록 조회
    List<DriveLog> findByCarId(Long carId);
    
    // 특정 기간 사이의 주행 기록 조회
    List<DriveLog> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);

    DriveLog getByCarId(Long carId);

    DriveLog findByCarIdAndStartTime(Long carId, LocalDateTime startTime);

    Optional<DriveLog> findFirstByCarIdAndEndTimeIsNullOrderByStartTimeDesc(int carId);
    
    // 특정 차량의 현재 진행 중인 주행기록 조회 (endTime이 null인 가장 최근 기록)
    @Query("SELECT d FROM DriveLog d WHERE d.carId = :carId AND d.endTime IS NULL ORDER BY d.startTime DESC")
    Optional<DriveLog> findActiveLogByCarId(@Param("carId") Long carId);
    
    // 활성 DriveLog의 endTime 정보 직접 업데이트
    @Modifying
    @Query("UPDATE DriveLog d SET d.endLatitude = :endLat, d.endLongitude = :endLon, d.endTime = :endTime, d.endPoint = :endPoint WHERE d.carId = :carId AND d.endTime IS NULL")
    int updateEndTimeForActiveDriveLog(@Param("carId") Long carId, 
                                       @Param("endLat") String endLatitude,
                                       @Param("endLon") String endLongitude, 
                                       @Param("endTime") LocalDateTime endTime,
                                       @Param("endPoint") String endPoint);


}
