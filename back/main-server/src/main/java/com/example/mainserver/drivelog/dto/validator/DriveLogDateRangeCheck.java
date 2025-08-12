package com.example.mainserver.drivelog.dto.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;
import java.time.LocalDate;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {DriveLogDateRangeValidator.class})
@Documented
public @interface DriveLogDateRangeCheck {

    String message() default "시작일과 종료일은 따로 혹은 시작일은 종료일보다 앞서는 경우는 없습니다.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
