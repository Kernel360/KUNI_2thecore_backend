package com.example._thecore_back.admin;

import com.example._thecore_back.admin.controller.dto.AdminRequest;
import com.example._thecore_back.admin.infrastructure.AdminRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test") // 테스트용 application-test.yml을 사용하도록 설정
public class AdminAcceptanceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AdminRepository adminRepository;

    @AfterEach
    void tearDown() {
        // 각 테스트 후 데이터 정리
        adminRepository.deleteAll();
    }

    @Test
    @DisplayName("관리자 등록 인수 테스트")
    void registerAdmin_Acceptance() throws Exception {
        // given
        AdminRequest request = new AdminRequest("acceptanceAdmin", "password", "인수테스트 관리자", "010-7777-8888", "acceptance@email.com",  LocalDate.parse("2000-01-01"));

        // when & then
        mockMvc.perform(post("/api/admins/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.loginId").value("acceptanceAdmin"));
    }

    @Test
    @DisplayName("관리자 등록 실패 인수 테스트 - ID 중복")
    void registerAdmin_Acceptance_Fail_DuplicateId() throws Exception {
        // given
        // 먼저 관리자를 한 명 등록
        AdminRequest initialRequest = new AdminRequest("duplicateAdmin", "password", "중복 관리자", "010-9999-0000", "duplicate@email.com", LocalDate.parse("2020-01-01"));
        mockMvc.perform(post("/api/admins/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(initialRequest)))
                .andExpect(status().isOk());


        mockMvc.perform(post("/api/admins/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(initialRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Login ID already exists."));
    }
}
