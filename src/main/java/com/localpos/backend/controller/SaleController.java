package com.localpos.backend.controller;

import com.localpos.backend.dto.ApiResponse;
import com.localpos.backend.dto.SaleRequest;
import com.localpos.backend.dto.SaleResponse;
import com.localpos.backend.service.SaleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sales")
@RequiredArgsConstructor
public class SaleController {

    private final SaleService saleService;

    @PostMapping
    public ResponseEntity<ApiResponse<SaleResponse>> createSale(@Valid @RequestBody SaleRequest request) {
        SaleResponse sale = saleService.createSale(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Sale created successfully", sale));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<SaleResponse>>> getAllSales(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder
    ) {
        Sort sort = sortOrder.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<SaleResponse> sales = saleService.getAllSales(startDate, endDate, pageable);

        return ResponseEntity.ok(ApiResponse.success(sales));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SaleResponse>> getSaleById(@PathVariable UUID id) {
        SaleResponse sale = saleService.getSaleById(id);
        return ResponseEntity.ok(ApiResponse.success(sale));
    }

    @GetMapping("/bill/{billNumber}")
    public ResponseEntity<ApiResponse<SaleResponse>> getSaleByBillNumber(@PathVariable String billNumber) {
        SaleResponse sale = saleService.getSaleByBillNumber(billNumber);
        return ResponseEntity.ok(ApiResponse.success(sale));
    }

    @GetMapping("/today")
    public ResponseEntity<ApiResponse<List<SaleResponse>>> getTodaySales() {
        List<SaleResponse> sales = saleService.getTodaySales();
        return ResponseEntity.ok(ApiResponse.success(sales));
    }
}
