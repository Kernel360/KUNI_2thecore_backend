package com.example.common.dto;

import com.example.common.group.CreateGroup;
import jakarta.validation.constraints.NotBlank;
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

    private String status;

    @NotBlank(groups = CreateGroup.class)
    private String carType;

    @NotBlank(groups = CreateGroup.class)
    private String carNumber;

    // @NotNull(groups = CreateGroup.class)
    private Double sumDist = 0.00;

    @NotBlank(groups = CreateGroup.class)
    private String lastLatitude;

    @NotBlank(groups = CreateGroup.class)
    private String lastLongitude;
}