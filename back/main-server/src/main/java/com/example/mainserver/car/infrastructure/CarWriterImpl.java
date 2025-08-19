package com.example.mainserver.car.infrastructure;

import com.example.common.domain.car.CarEntity;
import com.example.common.infrastructure.car.CarRepository;

import com.example.mainserver.car.domain.CarWriter;
import org.springframework.stereotype.Repository;

@Repository
public class CarWriterImpl implements CarWriter {
    private final CarRepository carRepository;

    public CarWriterImpl(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public CarEntity save(CarEntity carEntity){
        return carRepository.save(carEntity);
    }

    public void delete(CarEntity carEntity){
        carRepository.delete(carEntity);
    }
}
