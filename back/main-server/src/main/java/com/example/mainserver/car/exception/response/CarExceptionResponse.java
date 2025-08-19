package com.example.mainserver.car.exception.response;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CarExceptionResponse {

    private LocalDateTime timestamp;

    private int status;

    private String error;

    private String message;

    private String path;
}
