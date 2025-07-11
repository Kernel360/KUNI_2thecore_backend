package com.example._thecore_back.rest.car.model.dto;

import com.example._thecore_back.rest.car.db.CarStatus;
import com.example._thecore_back.rest.car.model.CreateGroup;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarRequestDto {
    @NotBlank(groups = CreateGroup.class)
    private String brand;

    @NotBlank(groups = CreateGroup.class)
    private String model;

    private Integer carYear;

    private CarStatus status;

    @NotBlank(groups = CreateGroup.class)
    private String carType;

    @NotBlank(groups = CreateGroup.class)
    private String carNumber;

    @NotNull(groups = CreateGroup.class)
    private double sumDist;

    @NotNull(groups = CreateGroup.class)
    private Integer emulatorId;
}
