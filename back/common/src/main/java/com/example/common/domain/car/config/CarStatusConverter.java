package com.example.common.domain.car.config;

import com.example.common.domain.car.CarStatus;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CarStatusConverter implements Converter<String, CarStatus> {
    @Override
    public CarStatus convert(String source) {
        if (source == null) return null;
        String v = source.trim();
        for (CarStatus s : CarStatus.values()) {
            if (s.getDisplayName().equals(v)) return s;
            if (s.name().equalsIgnoreCase(v)) return s;
        }
        throw new IllegalArgumentException("status가 존재하지 않습니다.: " + source);
    }
}
