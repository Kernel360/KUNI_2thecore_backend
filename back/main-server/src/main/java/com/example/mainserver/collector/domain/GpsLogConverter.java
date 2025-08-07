package com.example.mainserver.collector.domain;

import com.example.mainserver.collector.domain.dto.GpsLogDto;
import org.springframework.stereotype.Component;

@Component
public class GpsLogConverter {

    public GpsLogEntity toEntityByEmulatorId(GpsLogDto.Gps dto, int emulatorId){

        return GpsLogEntity.builder()
                .gpsLatitude(dto.getLatitude())
                .gpsLongitude(dto.getLongitude())
                .createdAt(dto.getTimeStamp())
                .emulatorId(emulatorId)
                .build();

    }

}
