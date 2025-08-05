package com.example.emulatorserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication(scanBasePackages = {
        "com.example.emulatorserver",
        "com.example.common"
})
@EnableJpaRepositories(basePackages = {
        "com.example.emulatorserver.device.infrastructure",
        "com.example.common.infrastructure.emulator",
        "com.example.common.infrastructure.car"
})
@EntityScan(basePackages = {
        "com.example.emulatorserver.device.domain",
        "com.example.common.domain"
})
public class EmulatorServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EmulatorServerApplication.class, args);
    }
}