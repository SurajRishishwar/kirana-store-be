package com.localpos.backend.service;

import com.localpos.backend.dto.DashboardResponse;
import com.localpos.backend.dto.ProductResponse;
import com.localpos.backend.repository.CustomerRepository;
import com.localpos.backend.repository.ProductRepository;
import com.localpos.backend.repository.SaleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final ProductService productService;

    @Transactional(readOnly = true)
    public DashboardResponse getDashboard() {
        LocalDateTime startOfDay = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.now().with(LocalTime.MAX);

        // Today's sales
        BigDecimal todaysSalesAmount = saleRepository.getTotalSalesAmount(startOfDay, endOfDay);
        long todaysBillsCount = saleRepository.countSales(startOfDay, endOfDay);
        long cashSalesCount = saleRepository.countSalesByPaymentMethod("CASH", startOfDay, endOfDay);
        long creditSalesCount = saleRepository.countSalesByPaymentMethod("CREDIT", startOfDay, endOfDay)
                + saleRepository.countSalesByPaymentMethod("PARTIAL", startOfDay, endOfDay);

        DashboardResponse.TodaySales todaySales = DashboardResponse.TodaySales.builder()
                .totalAmount(todaysSalesAmount != null ? todaysSalesAmount : BigDecimal.ZERO)
                .billsCount(todaysBillsCount)
                .cashSales(cashSalesCount)
                .creditSales(creditSalesCount)
                .build();

        // Credit outstanding
        BigDecimal totalCreditOutstanding = customerRepository.getTotalOutstandingCredit();
        long customersWithCreditCount = customerRepository.countCustomersWithCredit();

        DashboardResponse.CreditOutstanding creditOutstanding = DashboardResponse.CreditOutstanding.builder()
                .totalAmount(totalCreditOutstanding != null ? totalCreditOutstanding : BigDecimal.ZERO)
                .customersCount(customersWithCreditCount)
                .build();

        // Inventory
        long activeProductsCount = productRepository.countActiveProducts();
        long lowStockCount = productRepository.countLowStockProducts();
        long expiringCount = productService.getExpiringProducts().size();

        DashboardResponse.Inventory inventory = DashboardResponse.Inventory.builder()
                .activeProducts(activeProductsCount)
                .lowStockCount(lowStockCount)
                .expiringCount(expiringCount)
                .build();

        // Customers
        long totalCustomers = customerRepository.countActiveCustomers();
        LocalDateTime weekAgo = LocalDateTime.now().minusWeeks(1);
        long newCustomersThisWeek = customerRepository.findAll().stream()
                .filter(c -> c.getCreatedAt().isAfter(weekAgo))
                .count();

        DashboardResponse.Customers customers = DashboardResponse.Customers.builder()
                .total(totalCustomers)
                .newThisWeek(newCustomersThisWeek)
                .build();

        // Alerts
        List<ProductResponse> lowStockProducts = productService.getLowStockProducts();
        List<ProductResponse> expiringProducts = productService.getExpiringProducts();

        DashboardResponse.Alerts alerts = DashboardResponse.Alerts.builder()
                .lowStock(lowStockProducts)
                .expiring(expiringProducts)
                .build();

        return DashboardResponse.builder()
                .todaySales(todaySales)
                .creditOutstanding(creditOutstanding)
                .inventory(inventory)
                .customers(customers)
                .alerts(alerts)
                .build();
    }
}
