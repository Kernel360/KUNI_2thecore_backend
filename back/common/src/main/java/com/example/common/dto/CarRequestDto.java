package com.example.common.dto;

import com.example.common.group.CreateGroup;
import jakarta.validation.constraints.*;
import jakarta.validation.groups.Default;
import lombok.*;
import java.time.Year;

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
    @Pattern(regexp = "\\d{2,3}[가-힣]{1}\\d{4}", message = "차량 번호 형식이 올바르지 않습니다.", groups = CreateGroup.class)
    private String carNumber;

    // @NotNull(groups = CreateGroup.class)
    @Builder.Default
    private Double sumDist = 0.00;

    @NotNull(groups = CreateGroup.class)
    private String lastLatitude;

    @NotNull(groups = CreateGroup.class)
    private String lastLongitude;

    @AssertTrue(message = "차량 연식이 올바르지 않습니다.", groups = {CreateGroup.class, Default.class})
    private boolean isCarYearValid(){
        if (carYear == null){
            return true;
        }

        return carYear >= 1886 && carYear <= Year.now().getValue();
    }
}