package com.example.mainserver.auth.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class TokenDto {
    private String accessToken; // API 요청에 쓰이는 JWT
    private String refreshToken; // accessToken 갱신에 사용되는 JWT
}