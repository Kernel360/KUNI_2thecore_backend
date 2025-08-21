package hub.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DistanceCalculator {

    private static final double EARTH_RADIUS_KM = 6371.0; // 지구 반지름 (km)

    /**
     * Haversine 공식을 사용하여 두 GPS 좌표 간의 거리를 계산합니다.
     * 
     * @param lat1 첫 번째 위치의 위도
     * @param lon1 첫 번째 위치의 경도
     * @param lat2 두 번째 위치의 위도
     * @param lon2 두 번째 위치의 경도
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

            return distance;
        } catch (NumberFormatException e) {
            log.error("Invalid GPS coordinates: lat1={}, lon1={}, lat2={}, lon2={}", 
                     lat1, lon1, lat2, lon2, e);
            return 0.0;
        }
    }

    /**
     * 거리가 유효한 범위인지 확인합니다 (비정상적으로 큰 이동 방지)
     * 
     * @param distance 계산된 거리 (km)
     * @return 유효한 거리인지 여부
     */
    public static boolean isValidDistance(double distance) {
        // 5분간 최대 이동 가능 거리: 200km/h 기준 약 16.7km
        return distance >= 0 && distance <= 20.0;
    }
}