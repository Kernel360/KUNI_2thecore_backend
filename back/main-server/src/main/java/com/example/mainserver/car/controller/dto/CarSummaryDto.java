package com.example.mainserver.car.controller.dto;


import lombok.*;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarSummaryDto {

    private Long total;
    private Long driving;
    private Long idle;
    private Long maintenance;

}
