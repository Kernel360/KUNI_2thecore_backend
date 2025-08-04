package com.example.emulatorserver.device.infrastructure;

import com.example.common.domain.emulator.EmulatorEntity;
import com.example.common.domain.emulator.EmulatorReader;
import com.example.emulatorserver.device.infrastructure.emulator.EmulatorRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class EmulatorReaderImpl implements EmulatorReader {
    private final EmulatorRepository emulatorRepository;

    public EmulatorReaderImpl(EmulatorRepository emulatorRepository) {
        this.emulatorRepository = emulatorRepository;
    }

    @Override
    public Optional<EmulatorEntity> findById(int id) {
        return emulatorRepository.findById(id);
    }

    @Override
    public List<EmulatorEntity> findAll() {
        return emulatorRepository.findAll();
    }

    @Override
    // 확실하게 해당 아이디를 가진 애뮬레이터가 존재할 경우, 애뮬레이터를 조회하는 용도
    // 불필요하게 중복된 예외 처리를 하지 않기 위함
    public EmulatorEntity getById(int id) {
        return emulatorRepository.findById(id).orElse(null);
    }

    @Override
    public Optional<EmulatorEntity> findByCarNumber(String carNumber) {
        return emulatorRepository.findByCarNumber(carNumber);
    }
}
