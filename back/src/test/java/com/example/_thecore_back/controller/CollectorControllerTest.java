package com.example._thecore_back.controller;


import com.example._thecore_back.auth.domain.JwtTokenProvider;
import com.example._thecore_back.auth.infrastructure.JwtAuthenticationFilter;
import com.example._thecore_back.car.controller.CarController;
import com.example._thecore_back.collector.application.CollectorService;
import com.example._thecore_back.collector.controller.CollectorController;
import com.example._thecore_back.collector.domain.dto.GpsLogDto;
import com.example._thecore_back.collector.domain.dto.GpsLogResponseDto;
import com.example._thecore_back.collector.exception.CollectorEmulatorNotFoundException;
import com.example._thecore_back.collector.exception.GpsLogNotFoundException;
import com.example._thecore_back.emulator.exception.EmulatorNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CollectorController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
public class CollectorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CollectorService collectorService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("gpsLog 정상 응답")
    public void postGpsLogResponseTest() throws Exception {

        LocalDateTime now = LocalDateTime.of(2025, 7, 30, 12, 0, 0);

        GpsLogDto.Gps gps = new GpsLogDto.Gps("12.3212", "12.2312", now);

        GpsLogDto gpsLogDto = new GpsLogDto("12가1233", "loginId", List.of(gps), now, now.plusMinutes(1));


        when(collectorService.getGpsLog(any())).thenReturn(mockResponse());

        mockMvc.perform(post("/api/logs/gps")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gpsLogDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.device_id").value("abc"))
                .andExpect(jsonPath("$.data.start_time").exists())
                .andExpect(jsonPath("$.data.end_time").exists());



    }

    @Test
    @DisplayName("에뮬레이터가 존재하지 않을 경우 실패 응답")
    public void testEmulatorNotFound() throws Exception {

        LocalDateTime now = LocalDateTime.of(2025, 7, 30, 12, 0, 0);
        GpsLogDto.Gps gps = new GpsLogDto.Gps("12.3212", "12.2312", now);
        GpsLogDto gpsLogDto = new GpsLogDto("11111111", "loginId", List.of(gps), now, now.plusMinutes(1));

        // 예외 발생 시뮬레이션
        when(collectorService.getGpsLog(any()))
                .thenThrow(new CollectorEmulatorNotFoundException("11111111"));

        mockMvc.perform(post("/api/logs/gps")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gpsLogDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.message").value("11111111 에 대한 에뮬레이터가 존재하지 않습니다."));
    }

    @Test
    @DisplayName("로그가 존재하지 않을 경우 실패 응답")
    public void testGpsLogNotFound() throws Exception {

        LocalDateTime now = LocalDateTime.of(2025, 7, 30, 12, 0, 0);
        GpsLogDto.Gps gps = new GpsLogDto.Gps();
        GpsLogDto gpsLogDto = new GpsLogDto("11111111", "loginId", List.of(gps), now, now.plusMinutes(1));

        // 예외 발생 시뮬레이션
        when(collectorService.getGpsLog(any()))
                .thenThrow(new GpsLogNotFoundException());

        mockMvc.perform(post("/api/logs/gps")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(gpsLogDto)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.result").value(false))
                .andExpect(jsonPath("$.message").value("GPS 로그가 존재하지 않습니다."));
    }

    private GpsLogResponseDto mockResponse() {
        LocalDateTime now = LocalDateTime.of(2025, 7, 30, 12, 0, 0);
        return GpsLogResponseDto.builder()
                .deviceId("abc")
                .startTime(now)
                .endTime(now.plusMinutes(1))
                .build();
    }

}
