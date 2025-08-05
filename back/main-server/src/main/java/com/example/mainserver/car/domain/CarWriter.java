package com.example.mainserver.car.domain;

import com.example.common.domain.car.CarEntity;

public interface CarWriter {
    CarEntity save(CarEntity carEntity);

    void delete(CarEntity carEntity);
}
