package com.example.mainserver.auth.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AutoLoginRequest {
    private String accessToken;
    private String refreshToken;
}
