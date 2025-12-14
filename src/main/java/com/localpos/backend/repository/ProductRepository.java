package com.localpos.backend.repository;

import com.localpos.backend.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ProductRepository extends JpaRepository<Product, UUID> {

    Optional<Product> findByBarcode(String barcode);

    boolean existsByBarcode(String barcode);

    Page<Product> findByNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(
            String name, String category, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE p.stockQuantity < p.minStockLevel AND p.status = 'active'")
    List<Product> findLowStockProducts();

    @Query("SELECT p FROM Product p WHERE p.expiryDate IS NOT NULL AND p.expiryDate <= :date AND p.status = 'active'")
    List<Product> findExpiringProducts(@Param("date") LocalDate date);

    Page<Product> findByCategory(String category, Pageable pageable);

    Page<Product> findByStatus(String status, Pageable pageable);

    @Query("SELECT COUNT(p) FROM Product p WHERE p.status = 'active'")
    long countActiveProducts();

    @Query("SELECT COUNT(p) FROM Product p WHERE p.stockQuantity < p.minStockLevel AND p.status = 'active'")
    long countLowStockProducts();
}
