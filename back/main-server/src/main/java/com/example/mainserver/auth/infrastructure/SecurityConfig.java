package com.example.mainserver.auth.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Profile("!test")
@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * ✅ 1. Preflight (OPTIONS) 요청 전용 체인
     *  - CORS 프리플라이트 요청은 인증 없이 통과
     *  - WebConfig에서 이미 CORS 설정 중이므로 여기선 enable만 함
     */
    @Bean
    @Order(0)
    public SecurityFilterChain preflightChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher(request -> "OPTIONS".equalsIgnoreCase(request.getMethod()))
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll())
                .cors(cors -> cors.disable()) // WebConfig에서 이미 처리
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.disable())
                .securityContext(context -> context.disable())
                .requestCache(cache -> cache.disable());
        return http.build();
    }

    /**
     * ✅ 2. 일반 요청용 Security 필터 체인
     *  - WebConfig의 addCorsMappings()를 그대로 사용하도록 cors()만 활성화
     */
    @Bean
    @Order(1)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> {}) // WebMvcConfigurer의 CORS 설정 사용
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/swagger-resources/**",
                                "/swagger-ui.html",
                                "/webjars/**",
                                "/api/auth/login",
                                "/api/admin/signup",
                                "/actuator/**",
                                "/api/logs/**",
                                "/api/drivelogs/start",
                                "/api/drivelogs/end",
                                "/api/drivelogs/update-location",
                                "/api/drivelogs/excel"
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * ✅ 3. AuthenticationManager (JWT 인증용)
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * ✅ 4. PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
