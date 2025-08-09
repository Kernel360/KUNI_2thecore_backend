package com.example.mainserver.collector.domain;

import com.example.mainserver.collector.domain.dto.GpsLogDto;
import org.springframework.stereotype.Component;

@Component
public class GpsLogConverter {

    public GpsLogEntity toEntityByCarNumber(GpsLogDto.Gps dto, String carNumber){

        return GpsLogEntity.builder()
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .createdAt(dto.getTimestamp())
                .carNumber(carNumber)
                .build();

    }

}
