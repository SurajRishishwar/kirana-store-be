package com.localpos.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ProductRequest {
    @NotBlank(message = "Product name is required")
    private String name;

    private String description;
    private String category;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;

    @PositiveOrZero(message = "Cost price must be positive or zero")
    private BigDecimal costPrice;

    @NotNull(message = "Stock quantity is required")
    @PositiveOrZero(message = "Stock quantity must be positive or zero")
    private Integer stockQuantity;

    @PositiveOrZero(message = "Minimum stock level must be positive or zero")
    private Integer minStockLevel = 10;

    private String unit = "pcs";
    private String barcode;
    private LocalDate expiryDate;
    private String status = "active";
}
