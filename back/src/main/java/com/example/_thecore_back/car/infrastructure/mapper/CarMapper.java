package com.example._thecore_back.car.infrastructure.mapper;

import com.example._thecore_back.car.controller.dto.CarFilterRequestDto;
import com.example._thecore_back.car.domain.CarEntity;
import com.example._thecore_back.car.domain.CarStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CarMapper {
    List<CarEntity> search(CarFilterRequestDto carFilterRequestDto);
}
