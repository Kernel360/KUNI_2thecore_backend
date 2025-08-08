package com.example.mainserver;

import com.example.common.infrastructure.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = {
		"com.example.mainserver",
		"com.example.common",
})
@EntityScan(basePackages = {
		"com.example.mainserver.admin.domain",
		"com.example.mainserver.car.domain",
		"com.example.common.domain",
		"com.example.mainserver.collector.domain",
		"com.example.mainserver.drivelog.domain"
})
@EnableJpaRepositories(basePackages = {
		"com.example.mainserver.admin.infrastructure",
		"com.example.mainserver.car.infrastructure",
		"com.example.common.infrastructure",
		"com.example.mainserver.collector.infrastructure",
		"com.example.mainserver.drivelog.domain"
})
public class MainApplication {

	public static void main(String[] args) {
		SpringApplication.run(MainApplication.class, args);
	}
}