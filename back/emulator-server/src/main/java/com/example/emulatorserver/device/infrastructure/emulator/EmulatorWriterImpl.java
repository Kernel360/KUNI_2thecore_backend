package com.example.emulatorserver.device.infrastructure.emulator;

import com.example.emulatorserver.device.domain.emulator.EmulatorEntity;
import com.example.emulatorserver.device.domain.emulator.EmulatorWriter;
import org.springframework.stereotype.Repository;

@Repository
public class EmulatorWriterImpl implements EmulatorWriter {
    private final EmulatorRepository emulatorRepository;

    public EmulatorWriterImpl(EmulatorRepository emulatorRepository) {
        this.emulatorRepository = emulatorRepository;
    }

    @Override
    public EmulatorEntity save(EmulatorEntity emulator) {
        return emulatorRepository.save(emulator);
    }

    @Override
    public void delete(EmulatorEntity emulator) {
        emulatorRepository.delete(emulator);
    }
}
