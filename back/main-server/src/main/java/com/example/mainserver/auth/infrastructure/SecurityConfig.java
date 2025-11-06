package com.example.mainserver.auth.infrastructure;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
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
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.config.Customizer.withDefaults;

@Profile("!test")
@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // Swagger 관련 경로 화이트리스트
    private static final String[] SWAGGER_WHITELIST = {
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/swagger-resources/**",
            "/swagger-ui.html",
            "/webjars/**"
    };

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults())
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                SWAGGER_WHITELIST
                        ).permitAll()
                        .requestMatchers(
                                "/api/auth/login",
                                "/api/admin/signup",
                                "/actuator/prometheus",
                                "/actuator/health",
                                "/actuator/health/**",
                                "/actuator/info",
                                // 테스트용 로그인 우회
                                "/api/logs/**",
                                // 에뮬레이터 연동용: 주행 시작/종료 화이트리스트
                                "/api/drivelogs/start",
                                "/api/drivelogs/end",
                                // 허브 서버에서 호출하는 실시간 위치 업데이트 API
                                "/api/drivelogs/update-location",
                                // 엑셀 다운로드 API
                                "/api/drivelogs/excel"
                        ).permitAll()
                        //추가: 프리플라이트 OPTIONS 요청 항상 허용
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        //수정: setAllowedOrigins → setAllowedOriginPatterns 로 변경
        configuration.setAllowedOriginPatterns(List.of(
                "http://localhost:*",
                "https://2thecore.site",
                "https://api.2thecore.site",
                "https://2thecore-fe.s3-website.ap-northeast-2.amazonaws.com"
        ));

        //수정: 허용 메서드 정리
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

        //수정: 허용 헤더 명시적 지정 (CORS preflight 문제 방지)
        configuration.setAllowedHeaders(List.of(
                "Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin"
        ));

        //유지: 자격 증명(쿠키) 허용
        configuration.setAllowCredentials(true);

        //유지: 브라우저에서 노출할 응답 헤더 지정
        configuration.setExposedHeaders(List.of(
                "Authorization", "Set-Cookie", "new-access-token", "Content-Disposition"
        ));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
