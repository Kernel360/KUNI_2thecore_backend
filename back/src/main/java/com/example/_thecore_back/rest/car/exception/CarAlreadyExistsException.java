package com.example._thecore_back.rest.car.exception;

public class CarAlreadyExistsException extends RuntimeException {
    public CarAlreadyExistsException(String carNumber) {
      super("해당 차량 ( " + carNumber + " )이 이미 존재합니다. 다시 입력해주세요");
    }
}
