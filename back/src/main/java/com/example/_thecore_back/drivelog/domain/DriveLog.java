package com.example.mainserver.drivelog.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Entity
@Getter
public class DriveLog {

    @Id
    @Column(name = "drive_log_id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long driveLogId;

    @Column(name = "car_id", nullable = false)
    private Long carId;

    @Column(name = "start_point", nullable = false, length = 40)
    private String startPoint;

    @Column(name = "start_latitude", nullable = false, length = 45)
    private String startLatitude;

    @Column(name = "start_longitude", nullable = false, length = 45)
    private String startLongitude;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_point", nullable = false, length = 40)
    private String endPoint;

    @Column(name = "end_latitude", nullable = false, length = 45)
    private String endLatitude;

    @Column(name = "end_longitude", nullable = false,  length = 45)
    private String endLongitude;

    @Column(name = "end_time",  nullable = false)
    private LocalDateTime endTime;

    @Column(name = "drive_dist", precision = 10, scale = 2, nullable = false)
    private BigDecimal driveDist;

    @Column(length = 45, nullable = false)
    private String speed;

    @Column(name = "created_at", nullable = false, updatable = false) // 수정 불가
    private LocalDateTime createdAt;


    // JPA만 접근 가능
    protected DriveLog() {
    }

    // 생성자
    @Builder
    public DriveLog(Long driveLogId, Long carId, String startPoint, String startLatitude, String startLongitude,
                    LocalDateTime startTime, String endPoint, String endLatitude, String endLongitude,
                    LocalDateTime endTime, BigDecimal driveDist, String speed, LocalDateTime createdAt) {

        this.driveLogId = driveLogId;
        this.carId = carId;
        this.startPoint = startPoint;
        this.startLatitude = startLatitude;
        this.startLongitude = startLongitude;
        this.startTime = startTime;
        this.endPoint = endPoint;
        this.endLatitude = endLatitude;
        this.endLongitude = endLongitude;
        this.endTime = endTime;
        this.driveDist = driveDist;
        this.speed = speed;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

