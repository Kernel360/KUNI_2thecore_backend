package com.example.emulatorserver.device.exception.emulator;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateEmulatorException extends RuntimeException {
    public DuplicateEmulatorException(String message) {
        super(message);
    }
}
