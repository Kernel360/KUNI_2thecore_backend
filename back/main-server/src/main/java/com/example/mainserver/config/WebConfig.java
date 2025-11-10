package com.example.mainserver.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ContentNegotiationConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 관련 설정을 담당하는 클래스입니다.
 * CORS는 SecurityConfig에서 처리하므로, 이 클래스에서는 콘텐츠 협상만 설정합니다.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * URL 확장자 기반의 콘텐츠 타입 결정을 비활성화합니다.
     * 예: /example.js → text/javascript 로 잘못 판단되는 문제 방지
     */
    @Override
    public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
        configurer.favorPathExtension(false);
    }

    /*
     * ⚠️ 주의:
     * 기존 addCorsMappings() 메서드는 제거했습니다.
     * 이유:
     *  - Spring Security에서 이미 전역 CORS를 처리하고 있음
     *  - 두 레벨(CORSRegistry + SecurityConfig)에서 동시에 설정 시, OPTIONS 요청 충돌 발생
     *  - "Invalid CORS request" 또는 CORS 헤더 누락 현상이 생김
     *
     * CORS 설정은 SecurityConfig의 corsConfigurationSource()에서 전역 관리합니다.
     */
}
