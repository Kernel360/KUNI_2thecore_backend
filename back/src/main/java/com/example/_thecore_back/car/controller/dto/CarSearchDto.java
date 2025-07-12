package com.example._thecore_back.car.controller.dto;


import com.example._thecore_back.car.domain.CarEntity;
import lombok.*;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CarSearchDto {

    private String carNumber;

    private String brand;

    private String model;

    private String status;

    public static CarSearchDto EntityToDto(CarEntity car){
        return CarSearchDto.builder()
                .carNumber(car.getCarNumber())
                .brand(car.getBrand())
                .model(car.getModel())
                .status(car.getStatus().name())
                .build();

    }


}
