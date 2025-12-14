package com.localpos.backend.repository;

import com.localpos.backend.entity.Sale;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SaleRepository extends JpaRepository<Sale, UUID> {

    Optional<Sale> findByBillNumber(String billNumber);

    @Query("SELECT s FROM Sale s WHERE s.createdAt >= :startDate AND s.createdAt <= :endDate ORDER BY s.createdAt DESC")
    List<Sale> findSalesByDateRange(@Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);

    @Query("SELECT s FROM Sale s WHERE s.createdAt >= :startDate AND s.createdAt <= :endDate ORDER BY s.createdAt DESC")
    Page<Sale> findSalesByDateRange(@Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate,
                                     Pageable pageable);

    Page<Sale> findByPaymentStatus(String paymentStatus, Pageable pageable);

    Page<Sale> findByPaymentMethod(String paymentMethod, Pageable pageable);

    @Query("SELECT s FROM Sale s WHERE s.customer.id = :customerId ORDER BY s.createdAt DESC")
    Page<Sale> findByCustomerId(@Param("customerId") UUID customerId, Pageable pageable);

    // Analytics queries
    @Query("SELECT COALESCE(SUM(s.totalAmount), 0) FROM Sale s WHERE s.createdAt >= :startDate AND s.createdAt <= :endDate")
    BigDecimal getTotalSalesAmount(@Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(s) FROM Sale s WHERE s.createdAt >= :startDate AND s.createdAt <= :endDate")
    long countSales(@Param("startDate") LocalDateTime startDate,
                    @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COUNT(s) FROM Sale s WHERE s.paymentMethod = :method AND s.createdAt >= :startDate AND s.createdAt <= :endDate")
    long countSalesByPaymentMethod(@Param("method") String method,
                                    @Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COALESCE(SUM(s.amountPaid), 0) FROM Sale s WHERE s.createdAt >= :startDate AND s.createdAt <= :endDate")
    BigDecimal getTotalAmountPaid(@Param("startDate") LocalDateTime startDate,
                                   @Param("endDate") LocalDateTime endDate);

    @Query("SELECT COALESCE(SUM(s.creditAmount), 0) FROM Sale s WHERE s.createdAt >= :startDate AND s.createdAt <= :endDate")
    BigDecimal getTotalCreditAmount(@Param("startDate") LocalDateTime startDate,
                                     @Param("endDate") LocalDateTime endDate);
}
