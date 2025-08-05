package com.example.emulatorserver.device.controller;

import com.example.emulatorserver.device.application.LogService;
import com.example.emulatorserver.device.controller.dto.LogPowerDto;
import com.example.emulatorserver.device.exception.car.CarErrorCode;
import com.example.emulatorserver.device.exception.car.CarNotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class LogControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private LogService logService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("POST - 차량 시동 로그 컨트롤러 Test")
    void powerLogSuccess() throws Exception {
        // 요청 및 응답 객체 생성
        LogPowerDto logPowerDto = LogPowerDto.builder()
                .carNumber("12가3456")
                .loginId("Test")
                .powerStatus("ON")
                .build();

        // 기대 return 형식
        when(logService.changePowerStatus(any(LogPowerDto.class)))
                .thenReturn(logPowerDto);

        // test 실행
        ResultActions actions = mockMvc.perform(post("/api/logs/power")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(logPowerDto)));

        MvcResult result = actions.andReturn();
        System.out.println("-- response: " + result.getResponse().getContentAsString());
        System.out.println(result.getResponse().getStatus());

        // 결과 검증
        actions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.car_number").value("12가3456"))
                .andExpect(jsonPath("$.data.power_status").value("ON"));
    }

    @Test
    @DisplayName("POST - 차량 시동 로그 실패 : 존재하지 않는 차량 번호")
    void powerLogCarNotFound() throws Exception {
        // 요청 및 응답 객체 생성
        LogPowerDto logPowerDto = LogPowerDto.builder()
                .carNumber("12가3456")
                .loginId("Test")
                .powerStatus("ON")
                .build();

        // 기대 return 형식
        when(logService.changePowerStatus(any(LogPowerDto.class)))
                .thenThrow(new CarNotFoundException(CarErrorCode.CAR_NOT_FOUND_BY_NUMBER, "12가3456"));

        // test 실행
        ResultActions actions = mockMvc.perform(post("/api/logs/power")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(logPowerDto)));
        MvcResult result = actions.andReturn();
        System.out.println("-- response: " + result.getResponse().getContentAsString());
        System.out.println(result.getResponse().getStatus());
        // 결과 검증
        actions
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("해당 차량 ( 12가3456 )은 존재하지 않습니다. 다시 입력해주세요"));
    }
}
