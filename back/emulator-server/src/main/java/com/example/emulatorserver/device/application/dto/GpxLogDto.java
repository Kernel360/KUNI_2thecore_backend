package com.example.emulatorserver.device.application.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

@Builder
@Getter
@Setter
public class GpxLogDto {
    private String timeStamp;
    private double latitude; // 위도
    private double longitude; // 경도

    // Json화
    public static String listToJson(List<List<String>> bufferlist) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        List<GpxLogDto> gpxList = new java.util.ArrayList<>();

        // 정규표현식
        Pattern pattern = Pattern.compile("lat=\"(.*?)\"\\s+lon=\"(.*?)\"");

        for (List<String> log : bufferlist) {
            String timestamp = log.get(0);
            String trkpt = log.get(1);

            Matcher matcher = pattern.matcher(trkpt);
            if (matcher.find()) {
                String lat = matcher.group(1);
                String lon = matcher.group(2);

                GpxLogDto dto = GpxLogDto.builder()
                        .timeStamp(timestamp)
                        .latitude(Double.parseDouble(lat))
                        .longitude(Double.parseDouble(lon))
                        .build();
                gpxList.add(dto);
            }
        }

        return mapper.writeValueAsString(gpxList);
    }
}
