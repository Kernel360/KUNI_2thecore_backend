package com.example.mainserver.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * CORS (Cross-Origin Resource Sharing) 설정을 추가합니다.
     * 다른 출처(도메인)의 프론트엔드나 Swagger UI에서 오는 API 요청을 허용합니다.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 모든 경로에 대해 CORS 정책 적용
                .allowedOrigins(
                        // 허용할 출처(Origin) 목록
                        "http://localhost:3000",
                        "http://localhost:3001",
                        "http://2thecore.site",
                        "https://2thecore.site",
                        "http://2thecore-fe.s3-website.ap-northeast-2.amazonaws.com",
                        "http://43.203.110.104:8080" // Swagger UI 접속 주소
                )
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 허용할 HTTP 메서드
                .allowedHeaders("*") // 모든 요청 헤더 허용
                .allowCredentials(true) // 쿠키 및 인증 정보 허용
                .maxAge(3600); // Pre-flight 요청 캐시 시간(1시간)
    }

    /**
     * 콘텐츠 협상(Content Negotiation) 설정을 변경합니다.
     * URL 경로의 확장자(예: .js)를 기반으로 응답 타입을 결정하는 기능을 비활성화하여
     * 'Content-Type: text/javascript' 오류를 방지합니다.
     */
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.favorPathExtension(false);
    }
}