package com.example.common.dto;

import com.example.common.group.CreateGroup;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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

    @Min(value = 0, message = "차량 연식은 0 이상이어야 합니다.", groups = CreateGroup.class)
    private Integer carYear;

    private String status;

    @NotBlank(groups = CreateGroup.class)
    private String carType;

    @NotBlank(groups = CreateGroup.class)
    @Pattern(regexp = "\\d{2,3}[가-힣]{1}\\d{4}", message = "차량 번호 형식이 올바르지 않습니다.", groups = CreateGroup.class)
    private String carNumber;

    // @NotNull(groups = CreateGroup.class)
    @Min(value = 0, message = "주행 거리는 0 이상이어야 합니다.", groups = CreateGroup.class)
    private Double sumDist = 0.00;

    private String lastLatitude;

    private String lastLongitude;
}