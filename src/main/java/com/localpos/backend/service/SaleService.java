package com.localpos.backend.service;

import com.localpos.backend.dto.*;
import com.localpos.backend.entity.*;
import com.localpos.backend.exception.BadRequestException;
import com.localpos.backend.exception.ResourceNotFoundException;
import com.localpos.backend.repository.*;
import com.localpos.backend.security.UserPrincipal;
import com.localpos.backend.util.BillNumberGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SaleService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final CreditTransactionRepository creditTransactionRepository;
    private final StockMovementRepository stockMovementRepository;
    private final BillNumberGenerator billNumberGenerator;

    @Transactional
    public SaleResponse createSale(SaleRequest request) {
        // 1. Validate stock availability
        validateStockAvailability(request.getItems());

        // 2. Calculate totals
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalDiscount = BigDecimal.ZERO;

        List<SaleItem> saleItems = new ArrayList<>();

        for (SaleItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            BigDecimal itemTotal = product.getPrice().multiply(new BigDecimal(itemRequest.getQuantity()));
            BigDecimal itemDiscount = itemRequest.getDiscount().multiply(new BigDecimal(itemRequest.getQuantity()));

            subtotal = subtotal.add(itemTotal);
            totalDiscount = totalDiscount.add(itemDiscount);

            SaleItem saleItem = SaleItem.builder()
                    .product(product)
                    .productName(product.getName())
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(product.getPrice())
                    .discount(itemRequest.getDiscount())
                    .lineTotal(itemTotal.subtract(itemDiscount))
                    .build();

            saleItems.add(saleItem);
        }

        BigDecimal taxAmount = subtotal.subtract(totalDiscount).multiply(new BigDecimal("0.05"));
        BigDecimal totalAmount = subtotal.subtract(totalDiscount).add(taxAmount);
        BigDecimal creditAmount = totalAmount.subtract(request.getAmountPaid());

        // 3. Validate credit limit if applicable
        Customer customer = null;
        if (request.getCustomerId() != null) {
            customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

            if (creditAmount.compareTo(BigDecimal.ZERO) > 0) {
                if (customer.willExceedCreditLimit(creditAmount)) {
                    throw new BadRequestException(
                            String.format("Credit limit exceeded. Limit: ₹%s, Current: ₹%s, New credit: ₹%s",
                                    customer.getCreditLimit(), customer.getCreditBalance(), creditAmount));
                }
            }
        } else if (creditAmount.compareTo(BigDecimal.ZERO) > 0) {
            throw new BadRequestException("Customer required for credit sales");
        }

        // 4. Determine payment status
        String paymentStatus;
        if (creditAmount.compareTo(BigDecimal.ZERO) == 0) {
            paymentStatus = "PAID";
        } else if (request.getAmountPaid().compareTo(BigDecimal.ZERO) > 0) {
            paymentStatus = "PARTIAL";
        } else {
            paymentStatus = "CREDIT";
        }

        // 5. Create sale
//        String billNumber = billNumberGenerator.generateBillNumber();

        String billNumber =
                "BILL-" + Year.now().getValue() + "-"
                        + UUID.randomUUID().toString().substring(0, 8).toUpperCase();


        User currentUser = getCurrentUser();

        Sale sale = Sale.builder()
                .billNumber(billNumber)
                .customer(customer)
                .subtotal(subtotal)
                .discountAmount(totalDiscount)
                .taxAmount(taxAmount)
                .totalAmount(totalAmount)
                .amountPaid(request.getAmountPaid())
                .creditAmount(creditAmount)
                .paymentMethod(request.getPaymentMethod())
                .paymentStatus(paymentStatus)
                .notes(request.getNotes())
                .createdBy(currentUser)
                .build();

        // Add sale items
        for (SaleItem item : saleItems) {
            sale.addItem(item);
        }

        sale = saleRepository.save(sale);

        // 6. Update stock and create movements
        for (SaleItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId()).get();

            int stockBefore = product.getStockQuantity();
            int stockAfter = stockBefore - itemRequest.getQuantity();

            product.setStockQuantity(stockAfter);
            productRepository.save(product);

            StockMovement movement = StockMovement.builder()
                    .product(product)
                    .sale(sale)
                    .movementType("SALE")
                    .quantityChange(-itemRequest.getQuantity())
                    .stockBefore(stockBefore)
                    .stockAfter(stockAfter)
                    .notes("Sale " + billNumber)
                    .build();

            stockMovementRepository.save(movement);
        }

        // 7. Update customer credit if applicable
        if (customer != null && creditAmount.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal balanceBefore = customer.getCreditBalance();
            BigDecimal balanceAfter = balanceBefore.add(creditAmount);

            customer.setCreditBalance(balanceAfter);
            customer.setTotalPurchases(customer.getTotalPurchases() + 1);
            customer.setTotalSpent(customer.getTotalSpent().add(request.getAmountPaid()));
            customerRepository.save(customer);

            CreditTransaction creditTxn = CreditTransaction.builder()
                    .customer(customer)
                    .sale(sale)
                    .transactionType("CREDIT_TAKEN")
                    .amount(creditAmount)
                    .balanceBefore(balanceBefore)
                    .balanceAfter(balanceAfter)
                    .notes("Credit from sale " + billNumber)
                    .createdBy(currentUser)
                    .build();

            creditTransactionRepository.save(creditTxn);
        } else if (customer != null) {
            customer.setTotalPurchases(customer.getTotalPurchases() + 1);
            customer.setTotalSpent(customer.getTotalSpent().add(request.getAmountPaid()));
            customerRepository.save(customer);
        }

        return mapToSaleResponse(sale);
    }

    @Transactional(readOnly = true)
    public Page<SaleResponse> getAllSales(LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        Page<Sale> sales;

        if (startDate != null && endDate != null) {
            sales = saleRepository.findSalesByDateRange(startDate, endDate, pageable);
        } else {
            sales = saleRepository.findAll(pageable);
        }

        return sales.map(this::mapToSaleResponse);
    }

    @Transactional(readOnly = true)
    public SaleResponse getSaleById(UUID id) {
        Sale sale = saleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found with id: " + id));
        return mapToSaleResponse(sale);
    }

    @Transactional(readOnly = true)
    public SaleResponse getSaleByBillNumber(String billNumber) {
        Sale sale = saleRepository.findByBillNumber(billNumber)
                .orElseThrow(() -> new ResourceNotFoundException("Sale not found with bill number: " + billNumber));
        return mapToSaleResponse(sale);
    }

    @Transactional(readOnly = true)
    public List<SaleResponse> getTodaySales() {
        LocalDateTime startOfDay = LocalDateTime.now().with(LocalTime.MIN);
        LocalDateTime endOfDay = LocalDateTime.now().with(LocalTime.MAX);

        return saleRepository.findSalesByDateRange(startOfDay, endOfDay)
                .stream()
                .map(this::mapToSaleResponse)
                .collect(Collectors.toList());
    }

    private void validateStockAvailability(List<SaleItemRequest> items) {
        List<String> errors = new ArrayList<>();

        for (SaleItemRequest item : items) {
            Product product = productRepository.findById(item.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

            if (product.getStockQuantity() < item.getQuantity()) {
                errors.add(String.format("Insufficient stock for %s. Available: %d, Required: %d",
                        product.getName(), product.getStockQuantity(), item.getQuantity()));
            }
        }

        if (!errors.isEmpty()) {
            throw new BadRequestException(String.join("; ", errors));
        }
    }

    private User getCurrentUser() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private SaleResponse mapToSaleResponse(Sale sale) {
        List<SaleItemResponse> itemResponses = sale.getItems().stream()
                .map(item -> SaleItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProductName())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .discount(item.getDiscount())
                        .lineTotal(item.getLineTotal())
                        .build())
                .collect(Collectors.toList());

        CustomerResponse customerResponse = null;
        if (sale.getCustomer() != null) {
            Customer customer = sale.getCustomer();
            customerResponse = CustomerResponse.builder()
                    .id(customer.getId())
                    .name(customer.getName())
                    .phone(customer.getPhone())
                    .email(customer.getEmail())
                    .creditBalance(customer.getCreditBalance())
                    .build();
        }

        return SaleResponse.builder()
                .id(sale.getId())
                .billNumber(sale.getBillNumber())
                .customer(customerResponse)
                .subtotal(sale.getSubtotal())
                .discountAmount(sale.getDiscountAmount())
                .taxAmount(sale.getTaxAmount())
                .totalAmount(sale.getTotalAmount())
                .amountPaid(sale.getAmountPaid())
                .creditAmount(sale.getCreditAmount())
                .paymentMethod(sale.getPaymentMethod())
                .paymentStatus(sale.getPaymentStatus())
                .notes(sale.getNotes())
                .items(itemResponses)
                .createdAt(sale.getCreatedAt())
                .build();
    }
}
