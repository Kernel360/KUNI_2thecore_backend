package com.example.emulatorserver.emulator.application;

import com.example.emulatorserver.device.application.EmulatorService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

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
        EmulatorEntity emulator1 = this.emulatorEntity;
        CarEntity car1 = CarEntity.builder()
                .id(1)
                .emulatorId(emulator1.getId())
                .carNumber(emulator1.getCarNumber())
                .build();

        EmulatorEntity emulator2 = EmulatorEntity.builder()
                .id(2)
                .deviceId("z9y8x7w6-test-uuid")
                .status(EmulatorStatus.ON)
                .build();
        CarEntity car2 = CarEntity.builder()
                .id(2)
                .emulatorId(emulator2.getId())
                .carNumber("111가 1111")
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        List<EmulatorEntity> emulatorList = List.of(emulator1, emulator2);
        Page<EmulatorEntity> emulatorsPage = new PageImpl<>(emulatorList, pageable, emulatorList.size());

        when(emulatorRepository.findAll(any(Pageable.class))).thenReturn(emulatorsPage);
        when(carRepository.findByEmulatorId(emulator1.getId())).thenReturn(Optional.of(car1));
        when(carRepository.findByEmulatorId(emulator2.getId())).thenReturn(Optional.of(car2));

        Page<EmulatorEntity> resultPage = emulatorService.getAllEmulators(pageable);

        assertNotNull(resultPage);
        assertEquals(2, resultPage.getTotalElements());

        List<EmulatorEntity> content = resultPage.getContent();
        assertEquals(2, content.size());

        // 첫 번째 에뮬레이터 테스트
        assertEquals(emulator1.getDeviceId(), content.get(0).getDeviceId());
        assertEquals(car1.getCarNumber(), content.get(0).getCarNumber());

        // 두 번째 에뮬레이터 테스트
        assertEquals(emulator2.getDeviceId(), content.get(1).getDeviceId());
        assertEquals(car2.getCarNumber(), content.get(1).getCarNumber());

        verify(emulatorRepository).findAll(any(Pageable.class));
        verify(carRepository).findByEmulatorId(emulator1.getId());
        verify(carRepository).findByEmulatorId(emulator2.getId());
    }

    @Test
    @DisplayName("애뮬레이터 전체 조회 성공 - 애뮬레이터가 없을 때 빈 페이지 반환")
    void getAllEmulators_emptyPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<EmulatorEntity> emptyPage = Page.empty(pageable);

        when(emulatorRepository.findAll(any(Pageable.class))).thenReturn(emptyPage);

        Page<EmulatorEntity> result = emulatorService.getAllEmulators(pageable);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(emulatorRepository).findAll(any(Pageable.class));
        verify(carRepository, never()).findByEmulatorId(anyInt());
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
