package com.example.emulatorserver.device.infrastructure.car;

import com.example.emulatorserver.device.domain.car.CarEntity;
import com.example.emulatorserver.device.domain.car.CarReader;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class CarReaderImpl implements CarReader {

    private final CarRepository carRepository;

    public CarReaderImpl(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public Optional<CarEntity> findByCarNumber(String carNumber){
        return carRepository.findByCarNumber(carNumber);
    }

    public List<CarEntity> findAll(){
        return carRepository.findAll();
    }

    public Optional<CarEntity> findByEmulatorId(Integer emulatorId){
        return carRepository.findByEmulatorId(emulatorId);
    }
}
