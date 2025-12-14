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
public class StoreSettingResponse {
    private UUID id;
    private String storeName;
    private String contactNumber;
    private String address;
    private String currencySymbol;
    private BigDecimal taxRate;
    private Integer minStockDefault;
    private String receiptHeader;
    private String receiptFooter;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
