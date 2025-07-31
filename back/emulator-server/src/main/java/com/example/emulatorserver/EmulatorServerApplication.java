package com.example.emulatorserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class EmulatorServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmulatorServerApplication.class, args);
    }

}
