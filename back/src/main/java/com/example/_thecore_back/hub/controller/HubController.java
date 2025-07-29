package com.example._thecore_back.hub.controller;

import com.example._thecore_back.common.dto.ApiResponse;
import com.example._thecore_back.hub.application.HubService;
import com.example._thecore_back.hub.domain.dto.GpsLogDto;
import com.example._thecore_back.hub.domain.dto.GpsLogResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class HubController {

    private final HubService hubService;


    @PostMapping()
    public ApiResponse<GpsLogResponseDto> getGpsLog(@RequestBody GpsLogDto gpsLogDto) {

        var response =  hubService.getGpsLog(gpsLogDto);

        return ApiResponse.success(response);

    }
}
