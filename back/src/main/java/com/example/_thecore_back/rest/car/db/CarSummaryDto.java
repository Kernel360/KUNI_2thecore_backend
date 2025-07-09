package com.example._thecore_back.rest.car.db;


import lombok.*;

import java.util.Map;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarSummaryDto {


    private Long total;
    private Long operating;
    private Long waiting;
    private Long inspecting;



}
