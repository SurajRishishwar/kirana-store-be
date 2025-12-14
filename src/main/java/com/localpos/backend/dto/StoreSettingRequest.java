package com.localpos.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class StoreSettingRequest {
    @NotBlank(message = "Store name is required")
    private String storeName;

    private String contactNumber;
    private String address;
    private String currencySymbol = "₹";

    @PositiveOrZero(message = "Tax rate must be positive or zero")
    private BigDecimal taxRate = new BigDecimal("5.00");

    @PositiveOrZero(message = "Minimum stock default must be positive or zero")
    private Integer minStockDefault = 10;

    private String receiptHeader;
    private String receiptFooter;
}
