package com.example.mainserver.admin.infrastructure;

import com.example.mainserver.admin.domain.AdminEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<AdminEntity, String> {

    Optional<AdminEntity> findByLoginId(String loginId);
}
