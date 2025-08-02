package com.example._thecore_back.car.controller;

import com.example._thecore_back.car.controller.dto.CarRequestDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false) // 시큐리티 필터 생략
public class CarControllerTest2 {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCreateCar() throws Exception {
        CarRequestDto request = CarRequestDto.builder()
                .brand("Hyundai")
                .model("Sonata")
                .carYear(2020)
                .status("운행")
                .carType("Sedan")
                .carNumber("12가1289") // 차대번호는 테스트 마다 변경 필요..
                .sumDist(12345.9)
                .build();

        String json = objectMapper.writeValueAsString(request);

        MvcResult result = mockMvc.perform(post("/api/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(true))
                .andExpect(jsonPath("$.data.brand").value("Hyundai"))
                .andExpect(jsonPath("$.data.model").value("Sonata"))
                .andExpect(jsonPath("$.data.status").value("운행"))
                .andExpect(jsonPath("$.data.car_number").value("12가1289")) // 차대번호는 테스트 마다 변경 필요..
                .andExpect(jsonPath("$.data.sum_dist").value(12345.9))
                .andReturn(); // 결과 반환

        // 응답 본문 출력
        String responseBody = result.getResponse().getContentAsString();
        System.out.println("테스트 응답 결과:\n" + responseBody);
    }
}
