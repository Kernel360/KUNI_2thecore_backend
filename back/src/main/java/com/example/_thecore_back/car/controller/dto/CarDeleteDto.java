package com.example._thecore_back.car.controller.dto;

import com.example._thecore_back.car.domain.CarEntity;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarDeleteDto {
    private String model;
    private String brand;
    private String carNumber;

    public static CarDeleteDto EntityToDto(CarEntity carEntity) {
        return CarDeleteDto.builder()
                .model(carEntity.getModel())
                .brand(carEntity.getBrand())
                .carNumber(carEntity.getCarNumber())
                .build();
    }
}
