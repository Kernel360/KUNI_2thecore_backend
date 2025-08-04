package com.example.emulatorserver.device.exception.car;
import com.example.common.dto.ApiResponse;
import com.example.emulatorserver.device.controller.LogController;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackageClasses =  LogController.class)
@Order(1)

public class CarExceptionHandler {

    // 차량이 존재하지 않을 때
    @ExceptionHandler(CarNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleCarNotFoundException(CarNotFoundException e, HttpServletRequest request) {

        var response = ApiResponse.fail(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}
