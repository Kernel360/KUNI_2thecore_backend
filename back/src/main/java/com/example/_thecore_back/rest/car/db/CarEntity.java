package com.example._thecore_back.rest.car.db;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@Entity(name = "car")
public class CarEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id; // PK

    private String model; // 모델명

    private String brand; // 브랜드명

    private String year; // 연식

    @Enumerated(EnumType.STRING)
    private CarStatus status; // 차량 상태

    String carType; // 차종

    String carNumber; // 차량 번호

    float sumDist; // 총 거리

    int emulatorId; // 연결된 애뮬레이터 아이디

}
