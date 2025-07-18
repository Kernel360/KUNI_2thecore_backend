package com.example._thecore_back.admin.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminRequest {
    private String loginId;
    private String password;
    private String name;
    private String phoneNumber;
    private String email;
    private LocalDate birthdate;
}
