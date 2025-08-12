package com.example.mainserver.drivelog.dto.validator;

import com.example.mainserver.drivelog.dto.DriveLogFilterRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.Annotation;
import java.time.LocalDate;

public class DriveLogDateRangeValidator implements ConstraintValidator<DriveLogDateRangeCheck, DriveLogFilterRequestDto> {

    @Override
    public boolean isValid(DriveLogFilterRequestDto driveLogFilterRequestDto, ConstraintValidatorContext constraintValidatorContext) {
        var s = driveLogFilterRequestDto.getStartTime();
        var e = driveLogFilterRequestDto.getEndTime();

        if (s != null && e != null){
            return !s.isAfter(e);
        }

        return s == null && e == null;

    }
}
