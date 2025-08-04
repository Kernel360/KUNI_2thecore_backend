package com.example.mainserver.car.controller.dto;


import com.example.common.domain.car.CarStatus;
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
