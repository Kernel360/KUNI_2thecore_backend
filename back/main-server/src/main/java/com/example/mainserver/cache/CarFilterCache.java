package com.example.mainserver.cache;

import com.example.common.wrapper.PageWrapper;
import com.example.mainserver.car.controller.dto.CarFilterRequestDto;
import com.example.mainserver.car.controller.dto.CarSearchDto;
import com.example.mainserver.car.infrastructure.mapper.CarMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CarFilterCache {

    private final CarMapper carMapper;

    @Cacheable(cacheNames = "cars:Filter", key = "T(String).format('v1|brand=%s|model=%s|status=%s|twoParam=%s|page=%d|size=%d|sort=%s', " +
            "(#carFilterRequestDto?.brand?:''), " +
            "(#carFilterRequestDto?.model?:''), " +
            "(#carFilterRequestDto?.status?.name()?:''), " +
            "(#carFilterRequestDto?.twoParam?:false), " +
            "#page, #size, 'carNumber:ASC')",
            sync = true  )
    public PageWrapper<CarSearchDto> getCarByFilterCached(CarFilterRequestDto carFilterRequestDto, int page, int size){
        log.info("getCarByFilterCached page={}, size={}", page, size);
        int offset = (page - 1) * size;
        var result =  carMapper.search(carFilterRequestDto, offset, size);
        var total = carMapper.countByFilter(carFilterRequestDto);

        return PageWrapper.of(result.stream().map(CarSearchDto::EntityToDto).toList(), total, offset, size);
//
    }

}
