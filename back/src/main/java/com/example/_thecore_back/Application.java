package com.example._thecore_back;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
		"com.example._thecore_back", // main 모듈
		"com.example.emulatorserver" // emulator-server 모듈
})
@EnableJpaRepositories(basePackages = "com.example._thecore_back")
@EntityScan("com.example._thecore_back")
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
