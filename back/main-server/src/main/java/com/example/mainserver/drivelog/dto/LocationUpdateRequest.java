package com.example.mainserver.drivelog.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocationUpdateRequest {
    private Long carId;
    private String newLatitude;
    private String newLongitude;
}