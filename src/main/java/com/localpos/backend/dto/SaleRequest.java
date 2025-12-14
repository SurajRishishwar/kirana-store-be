package com.localpos.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
public class SaleRequest {
    private UUID customerId; // null for walk-in

    @NotEmpty(message = "At least one item is required")
    @Valid
    private List<SaleItemRequest> items;

    @NotNull(message = "Payment method is required")
    private String paymentMethod; // CASH, UPI, CARD, CREDIT, PARTIAL

    @NotNull(message = "Amount paid is required")
    @PositiveOrZero(message = "Amount paid must be positive or zero")
    private BigDecimal amountPaid;

    private String notes;
}
