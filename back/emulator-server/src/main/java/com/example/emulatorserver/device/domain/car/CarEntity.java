package com.example.emulatorserver.device.domain.car;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity(name = "emulatorServerCar")

public class CarEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "car_id")
    private int id; // PK


    private String model; // 모델명


    private String brand; // 브랜드명


    @Column(name = "car_year")
    private Integer carYear; // 연식

    @Enumerated(EnumType.STRING)
    @Column(length = 40)
    private CarStatus status; // 차량 상태

    private String carType; // 차종

    private String carNumber; // 차량 번호

    private double sumDist; // 총 거리

    private Integer emulatorId; // 연결된 애뮬레이터 아이디
}
