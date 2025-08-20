package hub.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DistanceCalculatorTest {

    @Test
    void testCalculateDistance_SeoulToGangnam() {
        // 서울역 (37.5665, 126.9780) -> 강남역 (37.4979, 127.0276)
        String lat1 = "37.5665";
        String lon1 = "126.9780";
        String lat2 = "37.4979";
        String lon2 = "127.0276";

        double distance = DistanceCalculator.calculateDistance(lat1, lon1, lat2, lon2);

        // 실제 거리 확인
        System.out.println("Calculated distance: " + distance + " km");
        assertTrue(distance > 8.0 && distance < 12.0, 
                   "Distance should be around 9.5km, but was: " + distance);
    }

    @Test
    void testCalculateDistance_ShortDistance() {
        // 100m 정도 거리
        String lat1 = "37.5665";
        String lon1 = "126.9780";
        String lat2 = "37.5675"; // 약 100m 북쪽
        String lon2 = "126.9780";

        double distance = DistanceCalculator.calculateDistance(lat1, lon1, lat2, lon2);

        // 약 0.1km
        assertTrue(distance > 0.05 && distance < 0.15, 
                   "Distance should be around 0.1km, but was: " + distance);
    }

    @Test
    void testIsValidDistance() {
        assertTrue(DistanceCalculator.isValidDistance(5.0));
        assertTrue(DistanceCalculator.isValidDistance(19.9));
        assertFalse(DistanceCalculator.isValidDistance(25.0));
        assertFalse(DistanceCalculator.isValidDistance(-1.0));
    }

    @Test
    void testCalculateDistance_InvalidCoordinates() {
        double distance = DistanceCalculator.calculateDistance("invalid", "126.9780", "37.4979", "127.0276");
        assertEquals(0.0, distance);
    }
}