package com.example._thecore_back.car.controller.dto;


import com.example._thecore_back.car.domain.CarStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class CarFilterRequestDto {


    private String carNumber;

    private String model;

    private String brand;

    private CarStatus status;

    @NotNull
    private boolean twoParam;


}
