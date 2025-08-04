package com.example.mainserver.auth.exception.exceptionhandler;

import com.example.common.dto.ApiResponse;
import com.example.common.exception.InvalidTokenException;
import com.example.mainserver.auth.exception.LoginFailedException;
import com.example.common.exception.TokenExpiredException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackages = "com.example._thecore_back.auth")
@Order(1)  // 높은 우선순위
public class AtGlobalExceptionHandler {

    @ExceptionHandler(LoginFailedException.class)
    public ResponseEntity<ApiResponse<?>> handleLoginFailed(LoginFailedException e) {
        log.warn("LoginFailedException handled: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.fail(e.getMessage()));
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiResponse<?>> handleInvalidToken(InvalidTokenException e) {
        log.warn("InvalidTokenException handled: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.fail(e.getMessage()));
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ApiResponse<?>> handleTokenExpired(TokenExpiredException e) {
        log.warn("TokenExpiredException handled: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.fail(e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleBadRequest(IllegalArgumentException e) {
        log.warn("IllegalArgumentException handled: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception e) {
        log.error("Unhandled exception in auth package", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail("서버 내부 오류가 발생했습니다."));
    }
}
