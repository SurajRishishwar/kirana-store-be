package com.localpos.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DashboardResponse {
    private TodaySales todaySales;
    private CreditOutstanding creditOutstanding;
    private Inventory inventory;
    private Customers customers;
    private Alerts alerts;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TodaySales {
        private BigDecimal totalAmount;
        private Long billsCount;
        private Long cashSales;
        private Long creditSales;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CreditOutstanding {
        private BigDecimal totalAmount;
        private Long customersCount;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Inventory {
        private Long activeProducts;
        private Long lowStockCount;
        private Long expiringCount;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Customers {
        private Long total;
        private Long newThisWeek;
    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Alerts {
        private List<ProductResponse> lowStock;
        private List<ProductResponse> expiring;
    }
}
