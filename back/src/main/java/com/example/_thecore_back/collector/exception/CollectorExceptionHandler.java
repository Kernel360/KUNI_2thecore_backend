package com.example._thecore_back.collector.exception;

import com.example._thecore_back.common.dto.ApiResponse;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Order(1)
public class CollectorExceptionHandler {

    @ExceptionHandler(GpsLogNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handlerGpsLogNotFoundException(GpsLogNotFoundException e) {

        var response = ApiResponse.fail(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler(CollectorEmulatorNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handlerCollectorEnulatorNotFoundException(CollectorEmulatorNotFoundException e) {

        var response = ApiResponse.fail(e.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);

    }




}
