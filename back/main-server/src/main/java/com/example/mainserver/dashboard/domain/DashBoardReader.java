package com.example.mainserver.dashboard.domain;

import com.example.mainserver.dashboard.controller.dto.TopCarModelDto;
import com.example.mainserver.dashboard.controller.dto.TopCarTypeDto;
import com.example.mainserver.dashboard.controller.dto.TopEndPointDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface DashBoardReader {

    TopCarModelDto findTopCarModelByDriveLog(LocalDateTime start, LocalDateTime end, Pageable pageable);

    TopEndPointDto findTopEndPointByDriveLog(LocalDateTime start, LocalDateTime end, Pageable pageable);

    TopCarTypeDto findTopCarTypeByDriveLog(LocalDateTime start, LocalDateTime end, Pageable pageable);



}
