package com.localpos.backend.controller;

import com.localpos.backend.dto.ApiResponse;
import com.localpos.backend.dto.CreditPaymentRequest;
import com.localpos.backend.dto.CreditTransactionResponse;
import com.localpos.backend.dto.CustomerResponse;
import com.localpos.backend.service.CreditService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/credit")
@RequiredArgsConstructor
public class CreditController {

    private final CreditService creditService;

    @PostMapping("/payment")
    public ResponseEntity<ApiResponse<Map<String, Object>>> recordPayment(
            @Valid @RequestBody CreditPaymentRequest request) {
        Map<String, Object> response = creditService.recordPayment(request);
        return ResponseEntity.ok(ApiResponse.success("Payment recorded successfully", response));
    }

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<ApiResponse<List<CreditTransactionResponse>>> getCustomerTransactions(
            @PathVariable UUID customerId) {
        List<CreditTransactionResponse> transactions = creditService.getCustomerTransactions(customerId);
        return ResponseEntity.ok(ApiResponse.success(transactions));
    }

    @GetMapping("/transactions")
    public ResponseEntity<ApiResponse<Page<CreditTransactionResponse>>> getAllTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<CreditTransactionResponse> transactions = creditService.getAllTransactions(pageable);
        return ResponseEntity.ok(ApiResponse.success(transactions));
    }

    @GetMapping("/outstanding")
    public ResponseEntity<ApiResponse<List<CustomerResponse>>> getOutstandingCreditAccounts() {
        List<CustomerResponse> customers = creditService.getOutstandingCreditAccounts();
        return ResponseEntity.ok(ApiResponse.success(customers));
    }

    @GetMapping("/total")
    public ResponseEntity<ApiResponse<BigDecimal>> getTotalOutstandingCredit() {
        BigDecimal total = creditService.getTotalOutstandingCredit();
        return ResponseEntity.ok(ApiResponse.success(total));
    }
}
