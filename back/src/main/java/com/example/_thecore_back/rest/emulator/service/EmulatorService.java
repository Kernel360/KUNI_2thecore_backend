package com.example._thecore_back.rest.emulator.service;

import com.example._thecore_back.rest.emulator.db.EmulatorEntity;
import com.example._thecore_back.rest.emulator.db.EmulatorRepository;
import com.example._thecore_back.rest.emulator.model.EmulatorConverter;
import com.example._thecore_back.rest.emulator.model.EmulatorRequest;
import com.example._thecore_back.rest.emulator.model.EmulatorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmulatorService {

    private final EmulatorRepository emulatorRepository;
    private final EmulatorConverter emulatorConverter;

    // 애뮬레이터 등록
    public EmulatorEntity registerEmulator(EmulatorRequest emulatorRequest) {
        EmulatorEntity entity = EmulatorEntity.builder()
                .carNumber(emulatorRequest.getCarNumber())  // 차량 번호
                .status(EmulatorStatus.OFF)  // 상태 설정 (초기값: OFF)
                .build();

        EmulatorEntity savedEntity;
        savedEntity = emulatorRepository.save(entity);

        return savedEntity;
    }

    // 애뮬레이터 상세 조회
    public EmulatorEntity getEmulator(Long id) {
        EmulatorEntity entity;
        entity = emulatorRepository.findById(id)
                .orElseThrow(() -> new EmulatorNotFoundException("해당하는 애뮬레이터가 없습니다: " + id));

        return entity;
    }

    // 애뮬레이터 전체 조회
    public List<EmulatorEntity> getAllEmulators() {

        return emulatorRepository.findAll();
    }

    // 애뮬레이터 수정
    public EmulatorEntity updateEmulator(Long id, EmulatorRequest emulatorRequest) {
        EmulatorEntity entity = emulatorRepository.findById(id)
                .orElseThrow(() -> new EmulatorNotFoundException("해당하는 애뮬레이터가 없습니다.: " + id));

        entity.setCarNumber(emulatorRequest.getCarNumber());

        return emulatorRepository.save(entity);
    }

    // 애뮬레이터 삭제
    public void deleteEmulator(Long id) {
        emulatorRepository.deleteById(id);  // 애뮬레이터 삭제
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    public static class EmulatorNotFoundException extends RuntimeException {
        public EmulatorNotFoundException(String message) {
            super(message);
        }
    }
}
