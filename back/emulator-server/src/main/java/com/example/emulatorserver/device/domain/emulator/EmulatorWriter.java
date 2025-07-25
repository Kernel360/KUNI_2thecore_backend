package com.example.emulatorserver.device.domain.emulator;

public interface EmulatorWriter {
    EmulatorEntity save(EmulatorEntity emulator);
    void delete(EmulatorEntity emulator);
}
