package com.example._thecore_back.car.exception;

public class CarNotFoundByFilterException extends RuntimeException {
    public CarNotFoundByFilterException() {
        super("해당 조건으로 검색된 차가 존재하지 않습니다.");
    }
}
