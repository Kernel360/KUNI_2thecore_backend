package com.example.mainserver.admin.exception.exceptionhandler;

import com.example.mainserver.admin.exception.AdminLoginIdAlreadyExistsException;
import com.example.common.dto.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.example._thecore_back.admin")
public class AdminExceptionHandler {

    @ExceptionHandler(AdminLoginIdAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<?>> handleAdminLoginIdAlreadyExists(AdminLoginIdAlreadyExistsException ex) {
        ApiResponse<?> response = ApiResponse.fail(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }
}
