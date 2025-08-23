package com.example.mainserver.dashboard.controller;


import com.example.common.dto.ApiResponse;
import com.example.mainserver.dashboard.application.DashboardService;
import com.example.mainserver.dashboard.controller.dto.RankByAnalyticsInLastMonthResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping()
    public ApiResponse<RankByAnalyticsInLastMonthResponseDto> getRankByAnalyticsInLastMonth(){

        var response  = dashboardService.getRankByAnalyticsInLastMonth();

        return ApiResponse.success(response);

    }

}
