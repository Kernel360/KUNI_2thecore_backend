package com.example.mainserver.admin.infrastructure;

import com.example.mainserver.admin.domain.AdminEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class AdminRepositoryTest {

    @Autowired
    private AdminRepository adminRepository;

    @Test
    @DisplayName("회원 가입 및 조회 테스트")
    void saveAndFindAdmin() {
        //AdminEntity 생성
        AdminEntity newAdmin = AdminEntity.builder()
                .loginId("testadmin")
                .password("password123")
                .name("테스트관리자")
                .phoneNumber("010-0000-0000")
                .email("test@test.com")
                .birthdate(LocalDate.parse("1990-01-01"))
                .build();

        //생성한 Entity 저장
        adminRepository.save(newAdmin);
        AdminEntity foundAdmin = adminRepository.findById("testadmin").orElse(null);


        assertThat(foundAdmin).isNotNull();
        assertThat(foundAdmin.getLoginId()).isEqualTo(newAdmin.getLoginId());
        assertThat(foundAdmin.getName()).isEqualTo(newAdmin.getName());
    }

    @Test
    @DisplayName("LoginID 존재 여부 확인 테스트")
    void existsByLoginId() {

        AdminEntity newAdmin = AdminEntity.builder()
                .loginId("testadmin")
                .password("password123")
                .name("테스트관리자")
                .phoneNumber("010-0000-0000")
                .email("test@test.com")
                .birthdate(LocalDate.parse("1990-01-01"))
                .build();
        adminRepository.save(newAdmin);


        boolean exists = adminRepository.existsById("testadmin");
        boolean notExists = adminRepository.existsById("non_existing_admin");


        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }
}
