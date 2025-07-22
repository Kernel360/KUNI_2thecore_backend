<<<<<<<< HEAD:back/src/main/java/com/example/_thecore_back/car/controller/dto/CarRequestDto.java
package com.example._thecore_back.car.controller.dto;
========
package com.example._thecore_back.car.model;
>>>>>>>> emulator:back/src/main/java/com/example/_thecore_back/car/model/CarRequest.java

import com.example._thecore_back.car.domain.CarStatus;
import com.example._thecore_back.car.validation.group.CreateGroup;
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

    private String status;

    @NotBlank(groups = CreateGroup.class)
    private String carType;

    @NotBlank(groups = CreateGroup.class)
    private String carNumber;

    @NotNull(groups = CreateGroup.class)
    private Double sumDist;
}
