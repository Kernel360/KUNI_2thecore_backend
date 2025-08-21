package com.example.mainserver.drivelog.domain;

import com.example.mainserver.drivelog.util.DistanceCalculator;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
@Entity
@Table(name = "drive_log")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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

    @Column(name = "created_at", nullable = false, updatable = false) // 수정 불가
    private LocalDateTime createdAt;

    private String memo;

    private String model;
    private String brand;


    // 생성자
    @Builder
    public DriveLog(Long driveLogId, Long carId, String startPoint, String startLatitude, String startLongitude,
                    LocalDateTime startTime, String endPoint, String endLatitude, String endLongitude,
                    LocalDateTime endTime, BigDecimal driveDist, String memo, LocalDateTime createdAt) {

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
        this.memo = memo;
        this.createdAt = createdAt;
    }

    // 시작/종료 좌표 기반으로 전체 거리 계산 (초기 생성 시)
    public void calculateDriveDist() {
        if (startLatitude != null && startLongitude != null && 
            endLatitude != null && endLongitude != null) {
            double distance = DistanceCalculator.calculateDistance(
                    startLatitude, startLongitude, endLatitude, endLongitude);
            this.driveDist = BigDecimal.valueOf(distance);
        } else {
            this.driveDist = BigDecimal.ZERO;
        }
    }

    // 새 좌표가 들어올 때마다 driveDist를 업데이트
    public double updateWithNewLocation(String newLatitude, String newLongitude) {
        double additionalDist = DistanceCalculator.calculateDistance(
                this.endLatitude != null ? this.endLatitude : startLatitude,
                this.endLongitude != null ? this.endLongitude : startLongitude,
                newLatitude, newLongitude
        );
        this.driveDist = this.driveDist.add(BigDecimal.valueOf(additionalDist));
        this.endLatitude = newLatitude;
        this.endLongitude = newLongitude;
        return additionalDist;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}

