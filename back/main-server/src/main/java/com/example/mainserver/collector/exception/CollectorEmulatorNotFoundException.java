package com.example.mainserver.collector.exception;

public class CollectorEmulatorNotFoundException extends RuntimeException {
    public CollectorEmulatorNotFoundException(String carNumber) {
        super(carNumber + " 에 대한 에뮬레이터가 존재하지 않습니다.");
    }
}
