package com.localpos.backend.repository;

import com.localpos.backend.entity.SaleItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface SaleItemRepository extends JpaRepository<SaleItem, UUID> {

    List<SaleItem> findBySaleId(UUID saleId);

    List<SaleItem> findByProductId(UUID productId);

    @Query("SELECT si.productName, SUM(si.quantity), SUM(si.lineTotal) " +
           "FROM SaleItem si " +
           "WHERE si.createdAt >= :startDate AND si.createdAt <= :endDate " +
           "GROUP BY si.productName " +
           "ORDER BY SUM(si.quantity) DESC")
    List<Object[]> getTopSellingProducts(@Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate);
}
