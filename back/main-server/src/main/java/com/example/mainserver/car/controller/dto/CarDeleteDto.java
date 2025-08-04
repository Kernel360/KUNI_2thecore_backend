package com.example.mainserver.car.controller.dto;

import com.example.common.domain.car.CarEntity;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)

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
