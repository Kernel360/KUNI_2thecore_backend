package com.example.emulatorserver.device.exception.car;

public class CarNotFoundException extends RuntimeException {

    public CarNotFoundException(CarErrorCode carErrorCode, Object... args) {
        super(carErrorCode.format(args));
    }

}
