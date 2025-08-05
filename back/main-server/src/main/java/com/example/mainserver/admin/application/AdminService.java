package com.example.mainserver.admin.application;

import com.example.mainserver.admin.domain.AdminEntity;
import com.example.mainserver.admin.infrastructure.AdminRepository;
import com.example.mainserver.admin.controller.dto.AdminRequest;
import com.example.mainserver.admin.controller.dto.AdminResponse;
import com.example.mainserver.admin.exception.AdminLoginIdAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class AdminService {

    private final AdminRepository adminRepository;

    @Autowired
    public AdminService(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
    }

    @Transactional
    public AdminResponse registerAdmin(AdminRequest requestDto) {
        // loginId 중복 확인
        if (adminRepository.existsById(requestDto.getLoginId())) {
            throw new AdminLoginIdAlreadyExistsException(requestDto.getLoginId());
        }

        AdminEntity adminEntity = AdminEntity.builder()
                .loginId(requestDto.getLoginId())
                .password(requestDto.getPassword())
                .name(requestDto.getName())
                .phoneNumber(requestDto.getPhoneNumber())
                .email(requestDto.getEmail())
                .birthdate(requestDto.getBirthdate())
                .authLevel("ADMIN") // 기본 권한 부여
                .build();

        AdminEntity savedEntity = adminRepository.save(adminEntity);
        return convertToDto(savedEntity);
    }

    @Transactional
    public AdminResponse updateAdmin(String loginId, AdminRequest requestDto) {
        AdminEntity adminEntity = adminRepository.findById(loginId)
                .orElseThrow(() -> new IllegalArgumentException("Admin not found with loginId: " + loginId));

        // 비밀번호 변경이 요청된 경우
        if (requestDto.getPassword() != null && !requestDto.getPassword().isEmpty()) {
            adminEntity.setPassword(requestDto.getPassword());
        }

        Optional.ofNullable(requestDto.getName()).ifPresent(adminEntity::setName);
        Optional.ofNullable(requestDto.getPhoneNumber()).ifPresent(adminEntity::setPhoneNumber);
        Optional.ofNullable(requestDto.getEmail()).ifPresent(adminEntity::setEmail);
        Optional.ofNullable(requestDto.getBirthdate()).ifPresent(adminEntity::setBirthdate);

        AdminEntity updatedEntity = adminRepository.save(adminEntity);
        return convertToDto(updatedEntity);
    }

    @Transactional
    public String deleteAdmin(String loginId) {
        if (!adminRepository.existsById(loginId)) {
            throw new IllegalArgumentException("Admin not found with loginId: " + loginId);
        }
        adminRepository.deleteById(loginId);
        return loginId;
    }

    private AdminResponse convertToDto(AdminEntity adminEntity) {
        return AdminResponse.builder()
                .loginId(adminEntity.getLoginId())
                .name(adminEntity.getName())
                .phoneNumber(adminEntity.getPhoneNumber())
                .email(adminEntity.getEmail())
                .birthdate(adminEntity.getBirthdate())
                .build();
    }
}