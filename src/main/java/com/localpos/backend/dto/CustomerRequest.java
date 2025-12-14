package com.localpos.backend.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CustomerRequest {
    @NotBlank(message = "Customer name is required")
    private String name;

    private String phone;

    @Email(message = "Email should be valid")
    private String email;

    private String address;

    @PositiveOrZero(message = "Credit limit must be positive or zero")
    private BigDecimal creditLimit = new BigDecimal("5000.00");

    private String status = "active";
}
