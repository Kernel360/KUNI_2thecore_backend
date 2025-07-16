package com.example._thecore_back.car.exception;

public class CarAlreadyExistsException extends RuntimeException {
    public CarAlreadyExistsException(String carNumber, Integer emulatorId) {
        super(buildMessage(carNumber, emulatorId));
    }

    private static String buildMessage(String carNumber, Integer emulatorId) {
        if (carNumber != null && emulatorId != null) {
            return String.format("이미 등록된 차량입니다. (차량 번호: %s, 에뮬레이터 ID: %d)", carNumber, emulatorId);
        } else if (carNumber != null) {
            return String.format("이미 등록된 차량 번호입니다: %s", carNumber);
        } else if (emulatorId != null) {
            return String.format("이미 등록된 에뮬레이터 ID입니다: %d", emulatorId);
        } else {
            return "이미 등록된 차량입니다.";
        }
    }
}
