package com.localpos.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customers", indexes = {
    @Index(name = "idx_customers_phone", columnList = "phone"),
    @Index(name = "idx_customers_name", columnList = "name")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Customer extends BaseEntity {

    @Column(nullable = false)
    private String name;

    @Column(length = 15)
    private String phone;

    private String email;

    @Column(columnDefinition = "TEXT")
    private String address;

    @Column(name = "credit_balance", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal creditBalance = BigDecimal.ZERO;

    @Column(name = "credit_limit", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal creditLimit = new BigDecimal("5000.00");

    @Column(name = "loyalty_points")
    @Builder.Default
    private Integer loyaltyPoints = 0;

    @Column(name = "total_purchases")
    @Builder.Default
    private Integer totalPurchases = 0;

    @Column(name = "total_spent", precision = 12, scale = 2)
    @Builder.Default
    private BigDecimal totalSpent = BigDecimal.ZERO;

    @Column(nullable = false)
    @Builder.Default
    private String status = "active"; // active, inactive, blocked

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    @Builder.Default
    private List<Sale> sales = new ArrayList<>();

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    @Builder.Default
    private List<CreditTransaction> creditTransactions = new ArrayList<>();

    // Helper method to check if customer has credit
    public boolean hasOutstandingCredit() {
        return creditBalance.compareTo(BigDecimal.ZERO) > 0;
    }

    // Helper method to check if credit limit will be exceeded
    public boolean willExceedCreditLimit(BigDecimal additionalCredit) {
        return creditBalance.add(additionalCredit).compareTo(creditLimit) > 0;
    }
}
