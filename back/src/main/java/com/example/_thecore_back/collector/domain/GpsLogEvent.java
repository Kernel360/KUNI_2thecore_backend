package com.example._thecore_back.collector.domain;

import com.example._thecore_back.collector.domain.dto.GpsLogDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class GpsLogEvent {

    private final GpsLogDto gpsLogDto;



}
