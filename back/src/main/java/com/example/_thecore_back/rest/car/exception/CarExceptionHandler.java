package com.example._thecore_back.rest.car.exception;


import com.example._thecore_back.rest.car.controller.CarController;
import com.example._thecore_back.rest.car.model.ApiResponse;
import com.example._thecore_back.rest.car.model.CarExceptionResponse;
import com.example._thecore_back.rest.car.model.CarResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice(basePackageClasses =  CarController.class)
@Order(1)

public class CarExceptionHandler {


    @ExceptionHandler(CarNotFoundException.class)
    public ApiResponse<CarExceptionResponse> handleCarNotFoundException(Exception e, HttpServletRequest request) {

        var response = CarExceptionResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(e.getMessage())
                .path(request.getRequestURI())
                .build();

        return ApiResponse.success(response);
    }

}
