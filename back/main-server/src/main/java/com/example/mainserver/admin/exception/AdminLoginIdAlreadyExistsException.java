package com.example.mainserver.admin.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT) //HTTP 409 Conflict
public class AdminLoginIdAlreadyExistsException extends RuntimeException {
    public AdminLoginIdAlreadyExistsException(String loginId) {
        super("이미 존재하는 아이디입니다: " + loginId);
    }
}
