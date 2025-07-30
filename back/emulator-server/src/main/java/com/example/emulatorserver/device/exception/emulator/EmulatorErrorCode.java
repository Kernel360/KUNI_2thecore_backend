package com.example.emulatorserver.device.exception.emulator;

public enum EmulatorErrorCode {

    EMULATOR_NOT_FOUND("해당 애뮬레이터 (%s)는 존재하지 않습니다. 다시 입력해주세요."),
    DUPLICATE_EMULATOR("해당 차량 (%s)에 이미 다른 에뮬레이터가 연결되어 있습니다."),
    CAR_FOR_EMULATOR_NOT_FOUND("애뮬레이터에 연결할 차량 (%s)이 존재하지 않습니다.");

    private final String message;

    EmulatorErrorCode(String message) {
        this.message = message;
    }

    public String format(Object... args) {
        return String.format(this.message, args);
    }

    public String getMessage() {
        return this.message;
    }
}
