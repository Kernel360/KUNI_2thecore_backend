package com.example.mainserver.auth.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AutoLoginResponse {
    private boolean valid;
    private String newAccessToken; // 새로 발급된 경우
    private String message;
}
