package com.example.mainserver.dashboard.controller.dto;


import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RankByAnalyticsInLastMonthResponseDto {

    private TopCarModelDto topCarModel;

    private TopEndPointDto topRegion;

    private TopCarTypeDto topCarType;

}
