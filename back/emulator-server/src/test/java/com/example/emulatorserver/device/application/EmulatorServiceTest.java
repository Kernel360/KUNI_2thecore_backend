package com.example.emulatorserver.device.application;

import com.example.emulatorserver.device.controller.dto.EmulatorRequest;
import com.example.emulatorserver.device.domain.car.CarEntity;
import com.example.emulatorserver.device.domain.emulator.EmulatorEntity;
import com.example.emulatorserver.device.domain.emulator.EmulatorStatus;
import com.example.emulatorserver.device.exception.emulator.CarNotFoundException;
import com.example.emulatorserver.device.exception.emulator.DuplicateEmulatorException;
import com.example.emulatorserver.device.exception.emulator.EmulatorNotFoundException;
import com.example.emulatorserver.device.infrastructure.car.CarRepository;
import com.example.emulatorserver.device.infrastructure.emulator.EmulatorRepository;
import org.mockito.Mock;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;
import java.util.Arrays;
import java.util.Optional;
import java.util.Collections;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class EmulatorServiceTest {

    @Mock
    private EmulatorRepository emulatorRepository;

    @Mock
    private CarRepository carRepository;

    @InjectMocks
    private EmulatorService emulatorService;

    private EmulatorRequest emulatorRequest;
    private CarEntity carEntity;
    private EmulatorEntity emulatorEntity;

    @BeforeEach
    void setUp() {
        emulatorRequest = new EmulatorRequest();
        emulatorRequest.setCarNumber("123가 4567");

        carEntity = CarEntity.builder()
                .id(1)
                .carNumber("123가 4567")
                .build();

        emulatorEntity = EmulatorEntity.builder()
                .id(1)
                .deviceId("a1b2c3d4-test-uuid")
                .status(EmulatorStatus.OFF)
                .build();
    }

    @Test
    @DisplayName("애뮬레이터 등록 성공")
    void registerEmulator_success() {
        when(carRepository.findByCarNumber(anyString())).thenReturn(Optional.of(carEntity));
        when(emulatorRepository.save(any(EmulatorEntity.class))).thenReturn(emulatorEntity);
        when(carRepository.save(any(CarEntity.class))).thenReturn(carEntity);

        EmulatorEntity result = emulatorService.registerEmulator(emulatorRequest);

        assertNotNull(result);
        assertEquals(emulatorEntity.getDeviceId(), result.getDeviceId());
        assertEquals(emulatorRequest.getCarNumber(), result.getCarNumber());
        verify(carRepository, times(1)).save(any(CarEntity.class));
        assertEquals(emulatorEntity.getId(), carEntity.getEmulatorId());
    }

    @Test
    @DisplayName("애뮬레이터 등록 실패 - 차량 없음")
    void registerEmulator_carNotFound() {
        when(carRepository.findByCarNumber(anyString())).thenReturn(Optional.empty());

        assertThrows(CarNotFoundException.class, () -> emulatorService.registerEmulator(emulatorRequest));
        verify(emulatorRepository, never()).save(any(EmulatorEntity.class));
        verify(carRepository, never()).save(any(CarEntity.class));
    }

    @Test
    @DisplayName("애뮬레이터 등록 실패 - 해당 차량에 이미 애뮬레이터 연결됨")
    void registerEmulator_carAlreadyHasEmulator() {
        carEntity.setEmulatorId(1);
        when(carRepository.findByCarNumber(anyString())).thenReturn(Optional.of(carEntity));

        assertThrows(DuplicateEmulatorException.class, () -> emulatorService.registerEmulator(emulatorRequest));
        verify(emulatorRepository, never()).save(any(EmulatorEntity.class));
        verify(carRepository, never()).save(any(CarEntity.class));
    }

    @Test
    @DisplayName("애뮬레이터 상세 조회 성공")
    void getEmulator_success() {
        String deviceId ="a1b2c3d4-test-uuid";
        when(emulatorRepository.findByDeviceId(deviceId)).thenReturn(Optional.of(emulatorEntity));
        when(carRepository.findByEmulatorId(emulatorEntity.getId())).thenReturn(Optional.of(carEntity));

        EmulatorEntity result = emulatorService.getEmulator(deviceId);

        assertNotNull(result);
        assertEquals(deviceId, result.getDeviceId());
        assertEquals("123가 4567", result.getCarNumber());
    }

    @Test
    @DisplayName("애뮬레이터 상세 조회 실패 - 애뮬레이터 없음")
    void getEmulator_notFound() {
        String deviceId ="a1b2c3d4-test-uuid";
        when(emulatorRepository.findByDeviceId(deviceId)).thenReturn(Optional.empty());

        assertThrows(EmulatorNotFoundException.class, () -> emulatorService.getEmulator(deviceId));
    }

    @Test
    @DisplayName("애뮬레이터 전체 조회 성공")
    void getAllEmulators_success() {
        EmulatorEntity emulator2 = EmulatorEntity.builder().id(2).deviceId("z9y8x7w6-test-uuid").status(EmulatorStatus.ON).build();
        CarEntity car2 = CarEntity.builder().id(2).carNumber("789나 0123").emulatorId(2).build();
        List<EmulatorEntity> emulators = Arrays.asList(emulatorEntity, emulator2);

        when(emulatorRepository.findAll()).thenReturn(emulators);
        when(carRepository.findByEmulatorId(emulatorEntity.getId())).thenReturn(Optional.of(carEntity));
        when(carRepository.findByEmulatorId(emulator2.getId())).thenReturn(Optional.of(car2));

        List<EmulatorEntity> result = emulatorService.getAllEmulators();

        assertNotNull(result);
        assertEquals(2, result.size());

        // 첫 번째 에뮬레이터
        assertEquals(emulatorEntity.getDeviceId(), result.get(0).getDeviceId());
        assertEquals(carEntity.getCarNumber(), result.get(0).getCarNumber());

        // 두 번째 에뮬레이터
        assertEquals(emulator2.getDeviceId(), result.get(1).getDeviceId());
        assertEquals(car2.getCarNumber(), result.get(1).getCarNumber());
    }

    @Test
    @DisplayName("애뮬레이터 전체 조회 성공 - 애뮬레이터가 없을 때 빈 리스트 반환")
    void getAllEmulators_emptyList() {
        when(emulatorRepository.findAll()).thenReturn(Collections.emptyList());

        List<EmulatorEntity> result = emulatorService.getAllEmulators();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("애뮬레이터 수정 성공 - 차량 번호 변경 없을 때")
    void updateEmulator_noCarNumberChange_success() {
        String deviceId = emulatorEntity.getDeviceId();
        emulatorRequest.setCarNumber("123가 4567");
        carEntity.setEmulatorId(emulatorEntity.getId());

        when(emulatorRepository.findByDeviceId(deviceId)).thenReturn(Optional.of(emulatorEntity));
        when(carRepository.findByEmulatorId(emulatorEntity.getId())).thenReturn(Optional.of(carEntity));
        when(carRepository.findByCarNumber("123가 4567")).thenReturn(Optional.of(carEntity));
        when(carRepository.save(any(CarEntity.class))).thenReturn(carEntity);

        EmulatorEntity result = emulatorService.updateEmulator(deviceId, emulatorRequest);

        assertNotNull(result);
        assertEquals("123가 4567", result.getCarNumber());
        verify(carRepository, times(1)).save(any(CarEntity.class));
    }

    @Test
    @DisplayName("애뮬레이터 수정 성공 - 차량 번호 변경 있을 때")
    void updateEmulator_carNumberChange_success() {
        String deviceId = emulatorEntity.getDeviceId();
        String newCarNumber = "새로운 차량 번호";
        EmulatorRequest updatedRequest = new EmulatorRequest(newCarNumber);

        CarEntity oldCarEntity = CarEntity.builder().id(1).carNumber("123가 4567").emulatorId(emulatorEntity.getId()).build();
        CarEntity newCarEntity = CarEntity.builder().id(2).carNumber(newCarNumber).build();

        when(emulatorRepository.findByDeviceId(deviceId)).thenReturn(Optional.of(emulatorEntity));
        when(carRepository.findByEmulatorId(emulatorEntity.getId())).thenReturn(Optional.of(oldCarEntity));
        when(carRepository.findByCarNumber(newCarNumber)).thenReturn(Optional.of(newCarEntity));

        EmulatorEntity result = emulatorService.updateEmulator(deviceId, updatedRequest);

        assertNotNull(result);
        assertEquals(newCarNumber, result.getCarNumber());
        assertNull(oldCarEntity.getEmulatorId());
        assertEquals(emulatorEntity.getId(), newCarEntity.getEmulatorId());
        verify(carRepository, times(2)).save(any(CarEntity.class));
    }

    @Test
    @DisplayName("애뮬레이터 수정 실패 - 애뮬레이터 없음")
    void updateEmulator_emulatorNotFound() {
        String deviceId = "a1b2c3d4-test-uuid";
        when(emulatorRepository.findByDeviceId(deviceId)).thenReturn(Optional.empty());

        assertThrows(EmulatorNotFoundException.class, () -> emulatorService.updateEmulator(deviceId, emulatorRequest));
    }

    @Test
    @DisplayName("애뮬레이터 수정 실패 - 변경할 차량 없음")
    void updateEmulator_newCarNotFound() {
        String deviceId = emulatorEntity.getDeviceId();
        String newCarNumber = "없는 차량 번호";
        EmulatorRequest updatedRequest = new EmulatorRequest(newCarNumber);

        when(emulatorRepository.findByDeviceId(deviceId)).thenReturn(Optional.of(emulatorEntity));
        when(carRepository.findByCarNumber(newCarNumber)).thenReturn(Optional.empty());

        assertThrows(CarNotFoundException.class, () -> emulatorService.updateEmulator(deviceId, updatedRequest));
        verify(emulatorRepository, never()).save(any(EmulatorEntity.class));
        verify(carRepository, never()).save(any(CarEntity.class));
    }

    @Test
    @DisplayName("애뮬레이터 수정 실패 - 변경할 차량에 이미 애뮬레이터 연결됨")
    void updateEmulator_newCarAlreadyHasEmulator() {
        String deviceId = emulatorEntity.getDeviceId();
        String newCarNumber = "새로운 차량 번호";
        EmulatorRequest updatedRequest = new EmulatorRequest(newCarNumber);

        CarEntity newCarEntityWithEmulator = CarEntity.builder().id(2).carNumber(newCarNumber).emulatorId(5).build();

        when(emulatorRepository.findByDeviceId(deviceId)).thenReturn(Optional.of(emulatorEntity));
        when(carRepository.findByCarNumber(newCarNumber)).thenReturn(Optional.of(newCarEntityWithEmulator));

        assertThrows(DuplicateEmulatorException.class, () -> emulatorService.updateEmulator(deviceId, updatedRequest));
        verify(emulatorRepository, never()).save(any(EmulatorEntity.class));
        verify(carRepository, never()).save(any(CarEntity.class));
    }

    @Test
    @DisplayName("애뮬레이터 수정 실패 - 변경할 carNumber에 이미 애뮬레이터 존재")
    void updateEmulator_duplicateEmulatorOnNewCarNumber() {
        String deviceId = emulatorEntity.getDeviceId();
        String newCarNumber = "새로운 차량 번호";
        EmulatorRequest updatedRequest = new EmulatorRequest(newCarNumber);

        CarEntity newCarEntity = CarEntity.builder().id(2).carNumber(newCarNumber).emulatorId(99).build(); // 다른 emulatorId

        when(emulatorRepository.findByDeviceId(deviceId)).thenReturn(Optional.of(emulatorEntity));
        when(carRepository.findByCarNumber(newCarNumber)).thenReturn(Optional.of(newCarEntity));

        assertThrows(DuplicateEmulatorException.class, () -> emulatorService.updateEmulator(deviceId, updatedRequest));
        verify(emulatorRepository, never()).save(any(EmulatorEntity.class));
        verify(carRepository, never()).save(any(CarEntity.class));
    }

    @Test
    @DisplayName("애뮬레이터 삭제 성공")
    void deleteEmulator_success() {
        String deviceId = "a1b2c3d4-test-uuid";
        carEntity.setEmulatorId(emulatorEntity.getId()); // 이미 연결된 상태로 설정
        when(emulatorRepository.findByDeviceId(deviceId)).thenReturn(Optional.of(emulatorEntity));
        when(carRepository.findByEmulatorId(emulatorEntity.getId())).thenReturn(Optional.of(carEntity));

        emulatorService.deleteEmulator(deviceId);

        assertNull(carEntity.getEmulatorId());
        verify(carRepository, times(1)).save(carEntity);
        verify(emulatorRepository, times(1)).delete(emulatorEntity);
    }

    @Test
    @DisplayName("애뮬레이터 삭제 실패 - 애뮬레이터 없음")
    void deleteEmulator_notFound() {
        String deviceId = "존재하지 않는 deviceId";
        when(emulatorRepository.findByDeviceId(deviceId)).thenReturn(Optional.empty());

        assertThrows(EmulatorNotFoundException.class, () -> emulatorService.deleteEmulator(deviceId));
        verify(emulatorRepository, never()).delete(any(EmulatorEntity.class));
        verify(carRepository, never()).save(any(CarEntity.class));
    }
}
