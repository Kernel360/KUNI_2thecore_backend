package com.example.mainserver.collector.controller;

import com.example.common.dto.ApiResponse;
import com.example.mainserver.collector.application.CollectorService;
import com.example.mainserver.collector.domain.dto.GpsLogDto;
import com.example.mainserver.collector.domain.dto.GpsLogResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class CollectorController {

    private final CollectorService collectorService;


    @PostMapping("/gps")
    public ApiResponse<GpsLogResponseDto> getGpsLog(@RequestBody GpsLogDto gpsLogDto) {

        var response =  collectorService.getGpsLog(gpsLogDto);

        return ApiResponse.success(response);

    }
}
