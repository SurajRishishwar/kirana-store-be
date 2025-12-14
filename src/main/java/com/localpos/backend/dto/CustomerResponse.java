package com.localpos.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerResponse {
    private UUID id;
    private String name;
    private String phone;
    private String email;
    private String address;
    private BigDecimal creditBalance;
    private BigDecimal creditLimit;
    private Integer loyaltyPoints;
    private Integer totalPurchases;
    private BigDecimal totalSpent;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
