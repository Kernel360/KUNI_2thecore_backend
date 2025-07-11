package com.example._thecore_back.rest.car.exception;



public class CarNotFoundException extends RuntimeException {

    public CarNotFoundException(String carNumber) {
        super("해당 차량 ( " + carNumber + " )은 존재하지 않습니다. 다시 입력해주세요");
    }

}
