package com.example.mainserver.drivelog.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DistanceCalculator {

    private static final double EARTH_RADIUS_KM = 6371.0; // 지구 반지름 (km)

    /**
     * Haversine 공식을 사용하여 두 GPS 좌표 간의 거리를 계산합니다.
     * 
     * @param lat1 시작 위치의 위도
     * @param lon1 시작 위치의 경도
     * @param lat2 종료 위치의 위도
     * @param lon2 종료 위치의 경도
     * @return 두 지점 간의 거리 (km)
     */
    public static double calculateDistance(String lat1, String lon1, String lat2, String lon2) {
        try {
            double lat1Rad = Math.toRadians(Double.parseDouble(lat1));
            double lon1Rad = Math.toRadians(Double.parseDouble(lon1));
            double lat2Rad = Math.toRadians(Double.parseDouble(lat2));
            double lon2Rad = Math.toRadians(Double.parseDouble(lon2));

            double deltaLat = lat2Rad - lat1Rad;
            double deltaLon = lon2Rad - lon1Rad;

            double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) +
                      Math.cos(lat1Rad) * Math.cos(lat2Rad) *
                      Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);

            double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
            double distance = EARTH_RADIUS_KM * c;

            log.debug("Distance calculated: {} km from ({},{}) to ({},{})", 
                     distance, lat1, lon1, lat2, lon2);

            return Math.round(distance * 100.0) / 100.0; // 소수점 2자리까지
        } catch (NumberFormatException e) {
            log.error("Invalid GPS coordinates: lat1={}, lon1={}, lat2={}, lon2={}", 
                     lat1, lon1, lat2, lon2, e);
            return 0.0;
        }
    }
}