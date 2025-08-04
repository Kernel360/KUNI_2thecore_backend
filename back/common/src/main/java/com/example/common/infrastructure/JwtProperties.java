package com.example.common.infrastructure;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "jwt")
@Getter
public class JwtProperties {
    private String secret;
    public void setSecret(String secret) {
        this.secret = secret;
    }
}
