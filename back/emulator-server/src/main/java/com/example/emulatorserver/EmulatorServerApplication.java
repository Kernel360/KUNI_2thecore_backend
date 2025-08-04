package com.example.emulatorserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.emulatorserver")
@EntityScan("com.example.emulatorserver")
public class EmulatorServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmulatorServerApplication.class, args);
    }

}
