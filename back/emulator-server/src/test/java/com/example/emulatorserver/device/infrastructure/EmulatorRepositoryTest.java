package com.example.emulatorserver.device.infrastructure;

import com.example.emulatorserver.device.domain.emulator.EmulatorEntity;
import com.example.emulatorserver.device.domain.emulator.EmulatorStatus;
import com.example.emulatorserver.device.infrastructure.emulator.EmulatorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class EmulatorRepositoryTest {

    @Autowired
    private EmulatorRepository emulatorRepository;

    private final String testCarNumber = "테스트차량1234";

    @Test
    @DisplayName("Emulator 저장 성공")
    void save_success() {
        // given
        EmulatorEntity emulator = EmulatorEntity.builder()
                .carNumber(testCarNumber)
                .status(EmulatorStatus.OFF)
                .build();

        // when
        EmulatorEntity savedEmulator = emulatorRepository.saveAndFlush(emulator);

        // then
        assertNotNull(savedEmulator.getId());
        assertNotNull(savedEmulator.getDeviceId());
        assertEquals(testCarNumber, savedEmulator.getCarNumber());
    }

    @Test
    @DisplayName("deviceId로 Emulator 조회 성공")
    void findByDeviceId_success() {
        // given
        EmulatorEntity emulator = EmulatorEntity.builder()
                .carNumber(testCarNumber)
                .status(EmulatorStatus.OFF)
                .build();

        EmulatorEntity savedEmulator = emulatorRepository.saveAndFlush(emulator);
        String generatedDeviceId = savedEmulator.getDeviceId();

        // when
        Optional<EmulatorEntity> foundEmulator = emulatorRepository.findByDeviceId(generatedDeviceId);

        // then
        assertTrue(foundEmulator.isPresent());
        assertEquals(generatedDeviceId, foundEmulator.get().getDeviceId());
    }

    @Test
    @DisplayName("존재하지 않는 deviceId로 조회 시 Optional.empty 반환")
    void findByDeviceId_notFound() {
        // when
        Optional<EmulatorEntity> foundEmulator = emulatorRepository.findByDeviceId("존재하지 않는 deviceId");

        // then
        assertFalse(foundEmulator.isPresent());
    }

    @Test
    @DisplayName("Emulator 저장 및 삭제 성공")
    void saveAndDeleteEmulator() {
        // given
        EmulatorEntity emulator = EmulatorEntity.builder()
                .carNumber(testCarNumber)
                .status(EmulatorStatus.ON)
                .build();

        EmulatorEntity saved = emulatorRepository.saveAndFlush(emulator);
        assertNotNull(saved.getId());

        // when
        emulatorRepository.delete(saved);
        emulatorRepository.flush();

        Optional<EmulatorEntity> foundAfterDelete = emulatorRepository.findById(saved.getId());

        // then
        assertFalse(foundAfterDelete.isPresent());
    }

    @Test
    @DisplayName("전체 Emulator 조회 성공")
    void findAllEmulators() {
        // given
        emulatorRepository.save(EmulatorEntity.builder().carNumber("1").status(EmulatorStatus.OFF).build());
        emulatorRepository.save(EmulatorEntity.builder().carNumber("2").status(EmulatorStatus.ON).build());

        // when
        List<EmulatorEntity> allEmulators = emulatorRepository.findAll();

        // then
        assertEquals(2, allEmulators.size());
    }
}
