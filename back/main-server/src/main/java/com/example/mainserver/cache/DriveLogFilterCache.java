package com.example.mainserver.cache;

import com.example.common.wrapper.PageWrapper;
import com.example.mainserver.drivelog.dto.DriveLogFilterRequestDto;
import com.example.mainserver.drivelog.dto.DriveLogFilterResponseDto;
import com.example.mainserver.drivelog.infrastructure.mapper.DriveLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class DriveLogFilterCache {

    private final DriveLogMapper driveLogMapper;

    @Cacheable(
            cacheNames = "driveLogs:Filter",
            key = "T(String).format('v1|car=%s|brand=%s|model=%s|status=%s|twoParam=%s|start=%s|end=%s|page=%d|size=%d|sort=%s:%s', " +
                    "(#p0?.carNumber?:''), " +
                    "(#p0?.brand?:''), " +
                    "(#p0?.model?:''), " +
                    "(#p0?.status != null ? #p0.status.name() : ''), " +
                    "(#p0?.twoParam?:false), " +
                    "(#p0?.startTime != null ? #p0.startTime.toString() : ''), " +
                    "(#p0?.endTime   != null ? #p0.endTime.toString()   : ''), " +
                    "#p1, #p2, " +
                    "(#p0?.sortBy?:'startTime'), " +
                    "(#p0?.sortOrder?:'ASC'))",
            sync = true
    )
    public PageWrapper<DriveLogFilterResponseDto> getDriveLogFiltersCache(
            DriveLogFilterRequestDto  driveLogFilterRequestDto, int page, int size) {

        log.info("getDriveLogByFilterCached page={}, size={}", page, size);

        int offset = (page - 1) * size;

        var result = driveLogMapper.search(driveLogFilterRequestDto, offset, size);

        var totalCount = driveLogMapper.countByFilter(driveLogFilterRequestDto);

        return PageWrapper.of(result, totalCount, page, size);

    }


}
