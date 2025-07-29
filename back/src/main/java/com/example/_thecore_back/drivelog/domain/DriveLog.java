package com.example._thecore_back.drivelog.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
public class DriveLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "drive_dist", precision = 10, scale = 2, nullable = false)
    private BigDecimal driveDist;

    @Column(length = 45, nullable = false)
    private String speed;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time",  nullable = false)
    private LocalDateTime endTime;

    @Column(name = "created_at", nullable = false, updatable = false) // 수정 불가
    private LocalDateTime createdAt;

    @Column(name = "car_id", nullable = false)
    private Long carId;

    @Column(name = "location_id", nullable = false)
    private Long locationId;


    // JPA만 접근 가능
    protected DriveLog() {
    }

    // 생성자
    @Builder
    public DriveLog(BigDecimal driveDist, String speed, LocalDateTime startTime, LocalDateTime endTime, Long carId, Long locationId){
        this.driveDist = driveDist;
        this.speed = speed;
        this.startTime = startTime;
        this.endTime = endTime;
        this.createdAt = LocalDateTime.now();
        this.carId = carId;
        this.locationId = locationId;
    }

    // Getter 생성
    public Long getId() {
        return id;
    }
    public BigDecimal getDriveDist() {
        return driveDist;
    }
    public String getSpeed() {
        return speed;
    }
    public LocalDateTime getStartTime() {
        return startTime;
    }
    public LocalDateTime getEndTime() {
        return endTime;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public Long getCarId() {
        return carId;
    }
    public Long getLocationId() {
        return locationId;
    }
}

