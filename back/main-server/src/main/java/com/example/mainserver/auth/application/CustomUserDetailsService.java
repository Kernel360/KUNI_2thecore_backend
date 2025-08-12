package com.example.mainserver.auth.application;

import com.example.mainserver.admin.domain.AdminEntity;
import com.example.mainserver.admin.infrastructure.AdminRepository;
import com.example.mainserver.auth.domain.AdminPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final AdminRepository adminRepository;

    @Override
    public UserDetails loadUserByUsername(String loginId) throws UsernameNotFoundException {
        AdminEntity admin = adminRepository.findByLoginId(loginId)
                .orElseThrow(() -> new UsernameNotFoundException("해당 관리자를 찾을 수 없음: " + loginId));

        return new AdminPrincipal(admin);
    }

}