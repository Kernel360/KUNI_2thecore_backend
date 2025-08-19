package com.example.mainserver.admin.controller;

import com.example.mainserver.admin.application.AdminService;
import com.example.mainserver.admin.controller.dto.AdminRequest;
import com.example.mainserver.admin.controller.dto.AdminResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import com.example.mainserver.auth.infrastructure.JwtAuthenticationFilter;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import com.example.common.domain.auth.JwtTokenProvider;

@WebMvcTest(controllers = AdminApiController.class, excludeAutoConfiguration = SecurityAutoConfiguration.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminService adminService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("회원가입 API 호출 성공")
    void registerAdminApi_Success() throws Exception {
        // AdminRequest 생성
        AdminRequest request = new AdminRequest("apiAdmin", "password", "API 관리자", "010-5555-6666", "api@email.com", LocalDate.parse("1998-01-01"));
        AdminResponse response = new AdminResponse("apiAdmin", "API 관리자", "010-5555-6666", "api@email.com", LocalDate.parse("1998-01-01"));

        given(adminService.registerAdmin(any(AdminRequest.class))).willReturn(response);

        mockMvc.perform(post("/api/admin/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.loginId").value("apiAdmin"))
                .andExpect(jsonPath("$.data.name").value("API 관리자"))
                .andDo(print());
    }
}