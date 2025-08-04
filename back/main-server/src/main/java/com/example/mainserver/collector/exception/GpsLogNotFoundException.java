package com.example.mainserver.collector.exception;

public class GpsLogNotFoundException extends RuntimeException {
    public GpsLogNotFoundException() {
        super("GPS 로그가 존재하지 않습니다.");
    }
}
