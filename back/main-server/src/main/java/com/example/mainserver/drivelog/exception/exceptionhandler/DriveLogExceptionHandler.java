package com.example.mainserver.drivelog.exception.exceptionhandler;

import ch.qos.logback.core.model.processor.ModelHandlerException;
import com.example.common.dto.ApiResponse;
import com.example.mainserver.car.exception.CarAlreadyExistsException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackages = "com.example.mainserver.drivelog")
@Order(1)
public class DriveLogExceptionHandler {

    // IllegalArgumentException 등 drivelog 특화 예외 처리 (400 Bad Request)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<?>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("IllegalArgumentException in DriveLog", e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.fail(e.getMessage()));
    }

    // 필요 시 추가 예외 처리 가능

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handle(MethodArgumentNotValidException e, HttpServletRequest request) {

        var response = ApiResponse.fail(e.getGlobalError().getDefaultMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);

    }
}
