package com.example.emulatorserver.emulator.application;

import com.example.emulatorserver.device.application.EmulatorService;
import com.example.emulatorserver.device.controller.dto.EmulatorRequest;
import com.example.emulatorserver.device.domain.car.CarEntity;
import com.example.emulatorserver.device.domain.emulator.EmulatorEntity;
import com.example.emulatorserver.device.domain.emulator.EmulatorStatus;
import com.example.emulatorserver.device.exception.car.CarErrorCode;
import com.example.emulatorserver.device.exception.car.CarNotFoundException;
import com.example.emulatorserver.device.exception.emulator.DuplicateEmulatorException;
import com.example.emulatorserver.device.exception.emulator.EmulatorErrorCode;
import com.example.emulatorserver.device.exception.emulator.EmulatorNotFoundException;
import com.example.emulatorserver.device.infrastructure.car.CarRepository;
import com.example.emulatorserver.device.infrastructure.emulator.EmulatorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

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
        emulatorRequest = new EmulatorRequest("123가 4567");

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
        // given
        when(carRepository.findByCarNumber(anyString())).thenReturn(Optional.of(carEntity));
        when(emulatorRepository.save(any(EmulatorEntity.class))).thenReturn(emulatorEntity);
        when(carRepository.save(any(CarEntity.class))).thenReturn(carEntity);

        // when
        EmulatorEntity result = emulatorService.registerEmulator(emulatorRequest);

        // then
        assertNotNull(result);
        assertEquals(emulatorEntity.getDeviceId(), result.getDeviceId());
        assertEquals(emulatorRequest.getCarNumber(), result.getCarNumber());
        assertEquals(emulatorEntity.getId(), carEntity.getEmulatorId());
        verify(carRepository, times(1)).save(any(CarEntity.class));
    }

    @Test
    @DisplayName("애뮬레이터 등록 실패 - 차량 없음")
    void registerEmulator_carNotFound() {
        // given
        String carNumber = "123가 4567";
        String expectedMessage = CarErrorCode.CAR_NOT_FOUND_BY_NUMBER.format(carNumber);
        when(carRepository.findByCarNumber(anyString())).thenReturn(Optional.empty());

        // when
        CarNotFoundException exception = assertThrows(CarNotFoundException.class, () -> {
            emulatorService.registerEmulator(emulatorRequest);
        });

        // then
        assertEquals(expectedMessage, exception.getMessage());
        verify(emulatorRepository, never()).save(any(EmulatorEntity.class));
        verify(carRepository, never()).save(any(CarEntity.class));
    }

    @Test
    @DisplayName("애뮬레이터 등록 실패 - 해당 차량에 이미 애뮬레이터 연결됨")
    void registerEmulator_carAlreadyHasEmulator() {
        // given
        String carNumber = "123가 4567";
        String expectedMessage = EmulatorErrorCode.DUPLICATE_EMULATOR.format(carNumber);
        carEntity.setEmulatorId(99);
        when(carRepository.findByCarNumber(anyString())).thenReturn(Optional.of(carEntity));

        // when
        DuplicateEmulatorException exception = assertThrows(DuplicateEmulatorException.class, () -> {
            emulatorService.registerEmulator(emulatorRequest);
        });

        // then
        assertEquals(expectedMessage, exception.getMessage());
        verify(emulatorRepository, never()).save(any(EmulatorEntity.class));
        verify(carRepository, never()).save(any(CarEntity.class));
    }

    @Test
    @DisplayName("애뮬레이터 상세 조회 성공")
    void getEmulator_success() {
        // given
        String deviceId = "a1b2c3d4-test-uuid";
        when(emulatorRepository.findByDeviceId(deviceId)).thenReturn(Optional.of(emulatorEntity));
        when(carRepository.findByEmulatorId(emulatorEntity.getId())).thenReturn(Optional.of(carEntity));

        // when
        EmulatorEntity result = emulatorService.getEmulator(deviceId);

        // then
        assertNotNull(result);
        assertEquals(deviceId, result.getDeviceId());
        assertEquals("123가 4567", result.getCarNumber());
    }

    @Test
    @DisplayName("애뮬레이터 상세 조회 실패 - 애뮬레이터 없음")
    void getEmulator_notFound() {
        // given
        String deviceId = "존재하지 않는 deviceId";
        String expectedMessage = EmulatorErrorCode.EMULATOR_NOT_FOUND.format(deviceId);
        when(emulatorRepository.findByDeviceId(deviceId)).thenReturn(Optional.empty());

        // when
        EmulatorNotFoundException exception = assertThrows(EmulatorNotFoundException.class, () -> {
            emulatorService.getEmulator(deviceId);
        });

        // then
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("애뮬레이터 전체 조회 성공")
    void getAllEmulators_success() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        List<EmulatorEntity> emulatorList = List.of(emulatorEntity);
        Page<EmulatorEntity> emulatorsPage = new PageImpl<>(emulatorList, pageable, emulatorList.size());

        when(emulatorRepository.findAll(any(Pageable.class))).thenReturn(emulatorsPage);
        when(carRepository.findByEmulatorId(emulatorEntity.getId())).thenReturn(Optional.of(carEntity));

        // when
        Page<EmulatorEntity> resultPage = emulatorService.getAllEmulators(pageable);

        // then
        assertNotNull(resultPage);
        assertEquals(1, resultPage.getTotalElements());
        assertEquals(emulatorEntity.getCarNumber(), resultPage.getContent().get(0).getCarNumber());
        verify(emulatorRepository).findAll(any(Pageable.class));
        verify(carRepository).findByEmulatorId(emulatorEntity.getId());
    }

    @Test
    @DisplayName("애뮬레이터 수정 성공")
    void updateEmulator_success() {
        // given
        String deviceId = emulatorEntity.getDeviceId();
        String newCarNumber = "새로운차량1234";
        EmulatorRequest newRequest = new EmulatorRequest(newCarNumber);

        CarEntity oldCar = CarEntity.builder().id(1).carNumber("123가 4567").emulatorId(emulatorEntity.getId()).build();
        CarEntity newCar = CarEntity.builder().id(2).carNumber(newCarNumber).build();

        when(emulatorRepository.findByDeviceId(deviceId)).thenReturn(Optional.of(emulatorEntity));
        when(carRepository.findByEmulatorId(emulatorEntity.getId())).thenReturn(Optional.of(oldCar));
        when(carRepository.findByCarNumber(newCarNumber)).thenReturn(Optional.of(newCar));

        // when
        EmulatorEntity result = emulatorService.updateEmulator(deviceId, newRequest);

        // then
        assertNotNull(result);
        assertEquals(newCarNumber, result.getCarNumber());
        assertNull(oldCar.getEmulatorId());
        assertEquals(emulatorEntity.getId(), newCar.getEmulatorId());
        verify(carRepository, times(2)).save(any(CarEntity.class));
    }

    @Test
    @DisplayName("애뮬레이터 수정 실패 - 애뮬레이터 없음")
    void updateEmulator_emulatorNotFound() {
        // given
        String deviceId = "not-found-id";
        String expectedMessage = EmulatorErrorCode.EMULATOR_NOT_FOUND.format(deviceId);
        when(emulatorRepository.findByDeviceId(deviceId)).thenReturn(Optional.empty());

        // when
        EmulatorNotFoundException exception = assertThrows(EmulatorNotFoundException.class, () -> {
            emulatorService.updateEmulator(deviceId, emulatorRequest);
        });

        // then
        assertEquals(expectedMessage, exception.getMessage());
    }

    @Test
    @DisplayName("애뮬레이터 수정 실패 - 변경할 차량 없음")
    void updateEmulator_newCarNotFound() {
        // given
        String deviceId = emulatorEntity.getDeviceId();
        String newCarNumber = "없는 차량 번호";
        EmulatorRequest updatedRequest = new EmulatorRequest(newCarNumber);
        String expectedMessage = CarErrorCode.CAR_NOT_FOUND_BY_NUMBER.format(newCarNumber);

        when(emulatorRepository.findByDeviceId(deviceId)).thenReturn(Optional.of(emulatorEntity));
        when(carRepository.findByCarNumber(newCarNumber)).thenReturn(Optional.empty());

        // when
        CarNotFoundException exception = assertThrows(CarNotFoundException.class, () -> {
            emulatorService.updateEmulator(deviceId, updatedRequest);
        });

        // then
        assertEquals(expectedMessage, exception.getMessage());
        verify(carRepository, never()).save(any(CarEntity.class));
    }

    @Test
    @DisplayName("애뮬레이터 삭제 성공")
    void deleteEmulator_success() {
        // given
        String deviceId = "a1b2c3d4-test-uuid";
        carEntity.setEmulatorId(emulatorEntity.getId());
        when(emulatorRepository.findByDeviceId(deviceId)).thenReturn(Optional.of(emulatorEntity));
        when(carRepository.findByEmulatorId(emulatorEntity.getId())).thenReturn(Optional.of(carEntity));

        // when
        emulatorService.deleteEmulator(deviceId);

        // then
        assertNull(carEntity.getEmulatorId());
        verify(carRepository, times(1)).save(carEntity);
        verify(emulatorRepository, times(1)).delete(emulatorEntity);
    }

    @Test
    @DisplayName("애뮬레이터 삭제 실패 - 애뮬레이터 없음")
    void deleteEmulator_notFound() {
        // given
        String deviceId = "존재하지 않는 deviceId";
        String expectedMessage = EmulatorErrorCode.EMULATOR_NOT_FOUND.format(deviceId);
        when(emulatorRepository.findByDeviceId(deviceId)).thenReturn(Optional.empty());

        // when
        EmulatorNotFoundException exception = assertThrows(EmulatorNotFoundException.class, () -> {
            emulatorService.deleteEmulator(deviceId);
        });

        // then
        assertEquals(expectedMessage, exception.getMessage());
        verify(emulatorRepository, never()).delete(any(EmulatorEntity.class));
        verify(carRepository, never()).save(any(CarEntity.class));
    }
}