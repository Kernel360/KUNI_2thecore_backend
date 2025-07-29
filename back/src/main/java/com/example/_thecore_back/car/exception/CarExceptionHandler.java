package com.example._thecore_back.car.exception;


import com.example._thecore_back.car.controller.CarController;
import com.example._thecore_back.car.exception.response.CarExceptionResponse;
import com.example._thecore_back.common.dto.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice(basePackageClasses =  CarController.class)
@Order(1)

public class CarExceptionHandler {

    // 차량이 이미 존재할 때 - create
    @ExceptionHandler(CarAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<?>> handleCarAlreadyExistsException(CarAlreadyExistsException e, HttpServletRequest request) {
//        var response = CarExceptionResponse.builder()
//                .timestamp(LocalDateTime.now())
//                .status(HttpStatus.CONFLICT.value())
//                .error(HttpStatus.CONFLICT.getReasonPhrase())
//                .message(e.getMessage())
//                .path(request.getRequestURI())
//                .build();

        var response = ApiResponse.fail(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);

//        return ResponseEntity
//                .status(HttpStatus.CONFLICT.value())
//                .body(ApiResponse.fail(response.getMessage()));
    }

    // 차량이 존재하지 않을 때
    @ExceptionHandler(CarNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleCarNotFoundException(CarNotFoundException e, HttpServletRequest request) {

//        var response = CarExceptionResponse.builder()
//                .timestamp(LocalDateTime.now())
//                .status(HttpStatus.NOT_FOUND.value())
//                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
//                .message(e.getMessage())
//                .path(request.getRequestURI())
//                .build();

//        return ResponseEntity
//                .status(HttpStatus.NOT_FOUND.value())
//                .body(ApiResponse.fail(response.getMessage()));

        var response = ApiResponse.fail(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
}
