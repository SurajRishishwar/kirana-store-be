package com.localpos.backend.repository;

import com.localpos.backend.entity.CreditTransaction;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface CreditTransactionRepository extends JpaRepository<CreditTransaction, UUID> {

    @Query("SELECT ct FROM CreditTransaction ct WHERE ct.customer.id = :customerId ORDER BY ct.createdAt DESC")
    List<CreditTransaction> findByCustomerId(@Param("customerId") UUID customerId);

    @Query("SELECT ct FROM CreditTransaction ct WHERE ct.customer.id = :customerId ORDER BY ct.createdAt DESC")
    Page<CreditTransaction> findByCustomerId(@Param("customerId") UUID customerId, Pageable pageable);

    @Query("SELECT ct FROM CreditTransaction ct WHERE ct.sale.id = :saleId")
    List<CreditTransaction> findBySaleId(@Param("saleId") UUID saleId);

    @Query("SELECT ct FROM CreditTransaction ct WHERE ct.transactionType = :type ORDER BY ct.createdAt DESC")
    Page<CreditTransaction> findByTransactionType(@Param("type") String type, Pageable pageable);

    @Query("SELECT ct FROM CreditTransaction ct WHERE ct.createdAt >= :startDate AND ct.createdAt <= :endDate ORDER BY ct.createdAt DESC")
    List<CreditTransaction> findTransactionsByDateRange(@Param("startDate") LocalDateTime startDate,
                                                         @Param("endDate") LocalDateTime endDate);
}
