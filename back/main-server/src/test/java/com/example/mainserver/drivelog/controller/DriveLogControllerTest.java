package com.example.mainserver.drivelog.controller;

import com.example.mainserver.drivelog.application.DriveLogService;
import com.example.mainserver.drivelog.domain.DriveLog;
import com.example.mainserver.drivelog.dto.DriveLogRequest;
import com.example.mainserver.drivelog.dto.DriveLogResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ActiveProfiles("test") // 'test' 프로필 지정
@WebMvcTest(controllers = DriveLogController.class)
@AutoConfigureMockMvc(addFilters = false) // Security 필터 작동 안 함
public class DriveLogControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DriveLogService driveLogService;


    @Autowired
    private ObjectMapper objectMapper;

    private DriveLogResponse toResponse(DriveLog log) {
        return DriveLogResponse.builder()
                .driveLogId(log.getDriveLogId())
                .carId(log.getCarId())
                .startPoint(log.getStartPoint())
                .startLatitude(log.getStartLatitude())
                .startLongitude(log.getStartLongitude())
                .startTime(log.getStartTime())
                .endPoint(log.getEndPoint())
                .endLatitude(log.getEndLatitude())
                .endLongitude(log.getEndLongitude())
                .endTime(log.getEndTime())
                .driveDist(log.getDriveDist())
                .speed(log.getSpeed())
                .createdAt(log.getCreatedAt())
                .build();
    }

    @Test
    @DisplayName("주행기록 저장 성공")
    public void createDriveLog_success() throws Exception {
        DriveLogRequest request = DriveLogRequest.builder()
                .carId(1L)
                .startPoint("서울")
                .startLatitude("37.5665")
                .startLongitude("126.9780")
                .startTime(LocalDateTime.now().minusHours(1))
                .endPoint("부산")
                .endLatitude("35.1796")
                .endLongitude("129.0756")
                .endTime(LocalDateTime.now())
                .driveDist(BigDecimal.valueOf(400))
                .speed("80km/h")
                .build();

        DriveLog savedLog = DriveLog.builder()
                .driveLogId(100L)
                .carId(request.getCarId())
                .startPoint(request.getStartPoint())
                .startLatitude(request.getStartLatitude())
                .startLongitude(request.getStartLongitude())
                .startTime(request.getStartTime())
                .endPoint(request.getEndPoint())
                .endLatitude(request.getEndLatitude())
                .endLongitude(request.getEndLongitude())
                .endTime(request.getEndTime())
                .driveDist(request.getDriveDist())
                .speed(request.getSpeed())
                .createdAt(LocalDateTime.now())
                .build();

        Mockito.when(driveLogService.save(Mockito.any(DriveLogRequest.class))).thenReturn(savedLog);

        mockMvc.perform(post("/api/drivelogs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", is(true)))
                .andExpect(jsonPath("$.message", is("주행기록 저장 완료")))
                .andExpect(jsonPath("$.data.driveLogId", is(100)))
                .andDo(result -> {
                    System.out.println("Response: " + result.getResponse().getContentAsString());
                });
    }

    @Test
    @DisplayName("전체 주행기록 조회 성공")
    public void getAllLogs_success() throws Exception {
        DriveLog log1 = DriveLog.builder()
                .driveLogId(1L)
                .carId(1L)
                .startPoint("서울")
                .startLatitude("37.5665")
                .startLongitude("126.9780")
                .startTime(LocalDateTime.now().minusDays(1))
                .endPoint("부산")
                .endLatitude("35.1796")
                .endLongitude("129.0756")
                .endTime(LocalDateTime.now().minusDays(1).plusHours(4))
                .driveDist(BigDecimal.valueOf(400))
                .speed("80km/h")
                .createdAt(LocalDateTime.now().minusDays(1))
                .build();

        DriveLog log2 = DriveLog.builder()
                .driveLogId(2L)
                .carId(2L)
                .startPoint("대구")
                .startLatitude("35.8714")
                .startLongitude("128.6014")
                .startTime(LocalDateTime.now().minusDays(2))
                .endPoint("광주")
                .endLatitude("35.1595")
                .endLongitude("126.8526")
                .endTime(LocalDateTime.now().minusDays(2).plusHours(3))
                .driveDist(BigDecimal.valueOf(200))
                .speed("70km/h")
                .createdAt(LocalDateTime.now().minusDays(2))
                .build();

        List<DriveLog> logs = List.of(log1, log2);

        Mockito.when(driveLogService.getAllLogs()).thenReturn(logs);

        mockMvc.perform(get("/api/drivelogs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result", is(true)))
                .andExpect(jsonPath("$.message", is("전체 주행기록 조회 완료")))
                .andExpect(jsonPath("$.data.length()", is(2)))
                .andDo(result -> {
                    System.out.println("Response: " + result.getResponse().getContentAsString());
                });
    }
}
