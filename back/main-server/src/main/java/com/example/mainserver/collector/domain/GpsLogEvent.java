package com.example.mainserver.collector.domain;

import com.example.mainserver.collector.domain.dto.GpsLogDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GpsLogEvent {

    private final GpsLogDto gpsLogDto;



}
