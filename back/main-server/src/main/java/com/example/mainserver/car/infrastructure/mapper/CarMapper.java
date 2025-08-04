package com.example.mainserver.car.infrastructure.mapper;

import com.example.common.domain.car.CarEntity;
import com.example.mainserver.car.controller.dto.CarFilterRequestDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CarMapper {
    List<CarEntity> search(@Param("filter") CarFilterRequestDto carFilterRequestDto,
                           @Param("offset") int offset,
                           @Param("limit") int limit);

    int countByFilter(@Param("filter") CarFilterRequestDto carFilterRequestDto);

}
