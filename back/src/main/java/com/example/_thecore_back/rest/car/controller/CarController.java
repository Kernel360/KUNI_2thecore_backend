package com.example._thecore_back.rest.car.controller;

import com.example._thecore_back.rest.car.db.CarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/vehicles")
public class CarController {
    private final CarRepository carRepository;

//    @GetMapping("")
//    public
}
