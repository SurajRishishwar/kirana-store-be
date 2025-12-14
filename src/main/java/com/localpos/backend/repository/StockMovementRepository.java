package com.localpos.backend.repository;

import com.localpos.backend.entity.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface StockMovementRepository extends JpaRepository<StockMovement, UUID> {

    @Query("SELECT sm FROM StockMovement sm WHERE sm.product.id = :productId ORDER BY sm.createdAt DESC")
    List<StockMovement> findByProductId(@Param("productId") UUID productId);

    @Query("SELECT sm FROM StockMovement sm WHERE sm.sale.id = :saleId")
    List<StockMovement> findBySaleId(@Param("saleId") UUID saleId);

    @Query("SELECT sm FROM StockMovement sm WHERE sm.movementType = :type ORDER BY sm.createdAt DESC")
    List<StockMovement> findByMovementType(@Param("type") String type);
}
