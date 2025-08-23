package com.example.mainserver.dashboard.application;


import com.example.mainserver.dashboard.controller.dto.RankByAnalyticsInLastMonthResponseDto;
import com.example.mainserver.dashboard.controller.dto.TopCarModelDto;
import com.example.mainserver.dashboard.controller.dto.TopCarTypeDto;
import com.example.mainserver.dashboard.controller.dto.TopEndPointDto;
import com.example.mainserver.dashboard.domain.DashBoardReader;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DashBoardReader dashboardReader;

    private static final int TOP_N = 3;

    public RankByAnalyticsInLastMonthResponseDto getRankByAnalyticsInLastMonth() {

        var today = ZonedDateTime.now(ZoneId.of("Asia/Seoul")).toLocalDate();

        var end = today.atStartOfDay();

        var start = end.minusMonths(1);

        var pageable = PageRequest.of(0, TOP_N);

        var topCarModel = getTopCarModel(start, end, pageable);

        var topRegion = getTopEndPoint(start, end, pageable);

        var topCarType = getTopCarType(start, end, pageable);

        return RankByAnalyticsInLastMonthResponseDto.builder()
                .topCarModel(topCarModel)
                .topRegion(topRegion)
                .topCarType(topCarType)
                .build();

    }

    private TopCarModelDto getTopCarModel(LocalDateTime start, LocalDateTime end, Pageable pageable) {
        return dashboardReader.findTopCarModelByDriveLog(start, end, pageable);
    }

    private TopEndPointDto getTopEndPoint(LocalDateTime start, LocalDateTime end, Pageable pageable) {
        return dashboardReader.findTopEndPointByDriveLog(start, end, pageable);
    }

    private TopCarTypeDto getTopCarType(LocalDateTime start, LocalDateTime end, Pageable pageable) {
        return dashboardReader.findTopCarTypeByDriveLog(start, end, pageable);
    }


}
