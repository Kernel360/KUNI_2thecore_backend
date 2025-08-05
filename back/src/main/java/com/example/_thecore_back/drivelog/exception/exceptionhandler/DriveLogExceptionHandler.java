package com.example.mainserver.drivelog.exception.exceptionhandler;

import com.example.mainserver.common.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackages = "com.example._thecore_back.drivelog")
public class DriveLogExceptionHandler {

    // IllegalArgumentException 등 drivelog 특화 예외 처리 (400 Bad Request)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("IllegalArgumentException in DriveLog", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(e.getMessage()));
    }

    // 필요 시 추가 예외 처리 가능
}
