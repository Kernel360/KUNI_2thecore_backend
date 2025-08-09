package com.example.mainserver.drivelog.infrastructure.mapper;

import com.example.mainserver.drivelog.dto.DriveLogFilterRequestDto;
import com.example.mainserver.drivelog.dto.DriveLogFilterResponseDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface DriveLogMapper {

    List<DriveLogFilterResponseDto> search(@Param("filter") DriveLogFilterRequestDto driveLogfilterRequestDto,
                                           @Param("offset") int offset,
                                           @Param("limit") int limit);

    int countByFilter(@Param("filter") DriveLogFilterRequestDto driveLogfilterRequestDto);

}
