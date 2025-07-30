package com.example.emulatorserver.device.exception.emulator;

import com.example.emulatorserver.common.dto.ApiResponse;
import com.example.emulatorserver.device.exception.car.CarNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.stream.Collectors;

@ControllerAdvice
public class EmulatorExceptionHandler {

    @ExceptionHandler(EmulatorNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleEmulatorNotFoundException(EmulatorNotFoundException ex) {
        ApiResponse<?> response = ApiResponse.fail(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(com.example.emulatorserver.device.exception.car.CarNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleCarNotFoundException(CarNotFoundException ex) {
        ApiResponse<?> response = ApiResponse.fail(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateEmulatorException.class)
    public ResponseEntity<ApiResponse<?>> handleDuplicateEmulatorException(DuplicateEmulatorException ex) {
        ApiResponse<?> response = ApiResponse.fail(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(","));
        ApiResponse<?> response = ApiResponse.fail(errorMessage);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
