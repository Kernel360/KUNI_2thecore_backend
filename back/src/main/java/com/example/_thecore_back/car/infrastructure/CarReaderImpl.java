package com.example._thecore_back.car.infrastructure;

import com.example._thecore_back.car.domain.CarEntity;
import com.example._thecore_back.car.domain.CarReader;
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

    public List<Object[]> getCountByStatus(){
        return carRepository.getCountByStatus();
    }

    public Optional<CarEntity> findByEmulatorId(Integer emulatorId){
        return carRepository.findByEmulatorId(emulatorId);
    }
}
