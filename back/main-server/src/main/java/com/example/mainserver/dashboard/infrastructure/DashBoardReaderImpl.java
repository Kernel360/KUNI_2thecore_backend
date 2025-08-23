package com.example.mainserver.dashboard.infrastructure;

import com.example.mainserver.dashboard.controller.dto.TopCarModelDto;
import com.example.mainserver.dashboard.controller.dto.TopCarTypeDto;
import com.example.mainserver.dashboard.controller.dto.TopEndPointDto;
import com.example.mainserver.dashboard.domain.DashBoardReader;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DashBoardReaderImpl implements DashBoardReader {

    private final DashboardRepository dashboardRepository;


    @Override
    public TopCarModelDto findTopCarModelByDriveLog(LocalDateTime start, LocalDateTime end, Pageable pageable) {
        var list = dashboardRepository.findTopCars(start, end, pageable);

        var model1 = !list.isEmpty() ? list.get(0) : null;
        var model2 = list.size() > 1 ? list.get(1) : null;
        var model3 = list.size() > 2 ? list.get(2) : null;

        return TopCarModelDto.builder()
                .model1(model1)
                .model2(model2)
                .model3(model3)
                .build();
    }

    @Override
    public TopEndPointDto findTopEndPointByDriveLog(LocalDateTime start, LocalDateTime end, Pageable pageable) {
        var list = dashboardRepository.findTopEndPoint(start, end, pageable);

        var region1 = !list.isEmpty() ? list.get(0) : null;
        var region2 = list.size() > 1 ? list.get(1) : null;
        var region3 = list.size() > 2 ? list.get(2) : null;

        return TopEndPointDto.builder()
                .region1(region1)
                .region2(region2)
                .region3(region3)
                .build();
    }

    @Override
    public TopCarTypeDto findTopCarTypeByDriveLog(LocalDateTime start, LocalDateTime end, Pageable pageable) {
        var list =  dashboardRepository.findTopCarType(start, end, pageable);
        var type1 = !list.isEmpty() ? list.get(0) : null;
        var type2 = list.size() > 1 ? list.get(1) : null;
        var type3 = list.size() > 2 ? list.get(2) : null;

        return TopCarTypeDto.builder()
                .type1(type1)
                .type2(type2)
                .type3(type3)
                .build();
    }
}
