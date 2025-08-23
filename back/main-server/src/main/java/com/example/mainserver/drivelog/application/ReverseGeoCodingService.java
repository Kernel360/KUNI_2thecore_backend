package com.example.mainserver.drivelog.application;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * Reverse Geocoding Service (좌표 → 행정구역명)
 * Kakao Local API coord2regioncode 사용.
 * x=경도(longitude), y=위도(latitude)
 */

@Service
@RequiredArgsConstructor
public class ReverseGeoCodingService {
    private static final Logger log = LoggerFactory.getLogger(ReverseGeoCodingService.class);

    private final RestTemplate restTemplate;


    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    public String reverseGeoCoding(String longitude, String latitude) {
        try{
            String url = String.format(
                    "https://dapi.kakao.com/v2/local/geo/coord2regioncode.json?x=%s&y=%s",
                    longitude, latitude
            );

            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "KakaoAK " + kakaoApiKey);

            if (log.isDebugEnabled()) {
                log.debug("coord2regioncode request url={} x={} y={} headers={}", url, longitude, latitude, headers);
            }

            ResponseEntity<Map> response = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(headers), Map.class
            );

            String bodyStr = String.valueOf(response.getBody());
            int maxLen = 800;
            String snippet = bodyStr.length() > maxLen
                    ? bodyStr.substring(0, maxLen) + "...(truncated,total=" + bodyStr.length() + ")"
                    : bodyStr;
            if (log.isDebugEnabled()) {
                log.debug("coord2regioncode response status={} body={}", response.getStatusCodeValue(), snippet);
            }


            if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
                if (log.isWarnEnabled()) {
                    log.warn("coord2regioncode non-2xx or empty body. status={} url={} x={} y={}",
                             response.getStatusCodeValue(), url, longitude, latitude);
                }
                return "주소 미확인";
            }

            Object docsObj = response.getBody().get("documents");
            if (!(docsObj instanceof List<?> docs) || docs.isEmpty()) {
                return "주소 미확인";
            }

            // 법정동("B")이 있으면 우선 사용, 없으면 첫 문서 사용
            for (Object o : docs) {
                if (o instanceof Map<?, ?> m && "B".equals(m.get("region_type"))) {
                    return formatAddress(m);
                }
            }
            // 법정동이 없을 경우, 반환된 주소 후보 중 첫 번째 문서를 사용
            return formatAddress((Map<?, ?>) docs.get(0));
        } catch (Exception e) {
            if (log.isWarnEnabled()) {
                log.warn("reverseGeoCoding failed. url={} x={} y={} error={}",
                         "https://dapi.kakao.com/v2/local/geo/coord2regioncode.json", longitude, latitude, e.toString());
            }
            // 네트워크 실패, 파싱 오류 등은 공통 메시지로 처리
            return "주소 미확인";
        }
    }

    private String formatAddress(Map<?, ?> region) {
        Object depth1 = region.get("region_1depth_name"); // 시/도
        Object depth2 = region.get("region_2depth_name"); // 시/구
        if (depth1 == null || depth2 == null) return "주소 미확인";
        return depth1 + " " + depth2;
    }

}
