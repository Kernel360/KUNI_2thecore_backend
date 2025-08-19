package com.example.mainserver.car.exception;



public class CarNotFoundException extends RuntimeException {

    public CarNotFoundException(CarErrorCode carErrorCode, Object... args) {
        super(carErrorCode.format(args));
    }

}
