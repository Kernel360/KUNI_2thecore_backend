package com.example._thecore_back.rest.car.model.dto;

import com.example._thecore_back.rest.car.db.CarEntity;
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
