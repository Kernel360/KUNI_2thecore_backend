package com.example.mainserver.car.controller.dto;

import com.example.common.domain.car.CarEntity;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarLocationDto {
    private String carNumber;
    private String status;
    private String lastLatitude;
    private String lastLongitude;

    public static CarLocationDto fromEntity(CarEntity car){
        return CarLocationDto.builder()
                .carNumber(car.getCarNumber())
                .status(car.getStatus().getDisplayName())
                .lastLatitude(car.getLastLatitude())
                .lastLongitude(car.getLastLongitude())
                .build();
    }
}
