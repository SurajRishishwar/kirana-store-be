package com.localpos.backend.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "store_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StoreSetting extends BaseEntity {

    @Column(name = "store_name", nullable = false)
    private String storeName;

    @Column(name = "contact_number", length = 15)
    private String contactNumber;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "currency_symbol")
    @Builder.Default
    private String currencySymbol = "₹";

    @Column(name = "tax_rate", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal taxRate = new BigDecimal("5.00");

    @Column(name = "min_stock_default")
    @Builder.Default
    private Integer minStockDefault = 10;

    @Column(name = "receipt_header", columnDefinition = "TEXT")
    private String receiptHeader;

    @Column(name = "receipt_footer", columnDefinition = "TEXT")
    private String receiptFooter;
}
