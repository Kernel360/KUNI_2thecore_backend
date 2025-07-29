package com.example._thecore_back.admin.infrastructure;

import com.example._thecore_back.admin.domain.AdminEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<AdminEntity, String> {

    Optional<Object> findByLoginId(String loginId);
}