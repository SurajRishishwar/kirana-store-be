package com.localpos.backend.repository;

import com.localpos.backend.entity.Customer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, UUID> {

    Optional<Customer> findByPhone(String phone);

    Page<Customer> findByNameContainingIgnoreCaseOrPhoneContaining(
            String name, String phone, Pageable pageable);

    @Query("SELECT c FROM Customer c WHERE c.creditBalance > 0 ORDER BY c.creditBalance DESC")
    List<Customer> findCustomersWithCredit();

    @Query("SELECT c FROM Customer c WHERE c.creditBalance > 0")
    Page<Customer> findCustomersWithCredit(Pageable pageable);

    @Query("SELECT c FROM Customer c ORDER BY c.totalSpent DESC")
    Page<Customer> findTopCustomers(Pageable pageable);

    @Query("SELECT SUM(c.creditBalance) FROM Customer c WHERE c.creditBalance > 0")
    BigDecimal getTotalOutstandingCredit();

    @Query("SELECT COUNT(c) FROM Customer c WHERE c.creditBalance > 0")
    long countCustomersWithCredit();

    @Query("SELECT COUNT(c) FROM Customer c WHERE c.status = 'active'")
    long countActiveCustomers();
}
