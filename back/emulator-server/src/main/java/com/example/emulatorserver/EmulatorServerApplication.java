package com.example.emulatorserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(scanBasePackages = {
        "com.example.common" // ← 공통 모듈 패키지 스캔 추가
})
@EnableJpaRepositories(basePackages = {
        "com.example.common.infrastructure"           // CarRepository 위치
})
@EntityScan(basePackages = {
        "com.example.common.domain"                   // CarEntity 위치
})
public class EmulatorServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EmulatorServerApplication.class, args);
    }
}