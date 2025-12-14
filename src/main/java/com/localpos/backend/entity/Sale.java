package com.localpos.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "sales", indexes = {
    @Index(name = "idx_sales_bill_number", columnList = "billNumber"),
    @Index(name = "idx_sales_customer", columnList = "customer_id"),
    @Index(name = "idx_sales_created_at", columnList = "created_at"),
    @Index(name = "idx_sales_status", columnList = "paymentStatus")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sale extends BaseEntity {

    @Column(name = "bill_number", unique = true, nullable = false)
    private String billNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "discount_amount", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal discountAmount = BigDecimal.ZERO;

    @Column(name = "tax_amount", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal taxAmount = BigDecimal.ZERO;

    @Column(name = "total_amount", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Column(name = "amount_paid", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal amountPaid = BigDecimal.ZERO;

    @Column(name = "credit_amount", precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal creditAmount = BigDecimal.ZERO;

    @Column(name = "payment_method", nullable = false)
    private String paymentMethod; // CASH, UPI, CARD, CREDIT, PARTIAL

    @Column(name = "payment_status", nullable = false)
    private String paymentStatus; // PAID, PARTIAL, CREDIT

    @Column(columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<SaleItem> items = new ArrayList<>();

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL)
    @Builder.Default
    private List<CreditTransaction> creditTransactions = new ArrayList<>();

    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL)
    @Builder.Default
    private List<StockMovement> stockMovements = new ArrayList<>();

    // Helper method to add sale item
    public void addItem(SaleItem item) {
        items.add(item);
        item.setSale(this);
    }

    // Helper method to remove sale item
    public void removeItem(SaleItem item) {
        items.remove(item);
        item.setSale(null);
    }

    // Helper method to check payment status
    public boolean isFullyPaid() {
        return "PAID".equals(paymentStatus);
    }

    public boolean hasCredit() {
        return creditAmount.compareTo(BigDecimal.ZERO) > 0;
    }
}
