package com.example._thecore_back.admin.application;

import com.example._thecore_back.admin.controller.dto.AdminRequest;
import com.example._thecore_back.admin.controller.dto.AdminResponse;
import com.example._thecore_back.admin.domain.AdminEntity;
import com.example._thecore_back.admin.infrastructure.AdminRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @InjectMocks
    private AdminService adminService;

    @Mock
    private AdminRepository adminRepository;

    @Test
    @DisplayName("회원가입 성공")
    void registerAdmin_Success() {
        // given
        AdminRequest request = new AdminRequest("newAdmin", "password", "새 관리자", "010-1111-2222", "new@email.com", LocalDate.parse("2000-01-01"));
        AdminEntity adminEntity = AdminEntity.builder()
                .loginId(request.getLoginId())
                .name(request.getName())
                .build();

        when(adminRepository.existsById(anyString())).thenReturn(false);
        when(adminRepository.save(any(AdminEntity.class))).thenReturn(adminEntity);

        // when
        AdminResponse response = adminService.registerAdmin(request);

        // then
        assertThat(response.getLoginId()).isEqualTo("newAdmin");
        assertThat(response.getName()).isEqualTo("새 관리자");
    }

    @Test
    @DisplayName("회원가입 실패 - LoginIdAlreadyExists")
    void registerAdmin_Fail_LoginIdAlreadyExists() {
        // given
        AdminRequest request = new AdminRequest("existingAdmin", "password", "기존 관리자", "010-3333-4444", "existing@email.com", LocalDate.parse("1995-01-01"));

        when(adminRepository.existsById("existingAdmin")).thenReturn(true);

        // when & then
        assertThrows(IllegalArgumentException.class, () -> {
            adminService.registerAdmin(request);
        });
    }
}
