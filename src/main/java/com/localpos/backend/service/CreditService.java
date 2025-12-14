package com.localpos.backend.service;

import com.localpos.backend.dto.CreditPaymentRequest;
import com.localpos.backend.dto.CreditTransactionResponse;
import com.localpos.backend.dto.CustomerResponse;
import com.localpos.backend.entity.CreditTransaction;
import com.localpos.backend.entity.Customer;
import com.localpos.backend.entity.User;
import com.localpos.backend.exception.BadRequestException;
import com.localpos.backend.exception.ResourceNotFoundException;
import com.localpos.backend.repository.CreditTransactionRepository;
import com.localpos.backend.repository.CustomerRepository;
import com.localpos.backend.repository.UserRepository;
import com.localpos.backend.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CreditService {

    private final CreditTransactionRepository creditTransactionRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    @Transactional
    public Map<String, Object> recordPayment(CreditPaymentRequest request) {
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found"));

        if (customer.getCreditBalance().compareTo(BigDecimal.ZERO) == 0) {
            throw new BadRequestException("Customer has no outstanding credit");
        }

        if (request.getAmount().compareTo(customer.getCreditBalance()) > 0) {
            throw new BadRequestException(
                    String.format("Payment amount (₹%s) exceeds credit balance (₹%s)",
                            request.getAmount(), customer.getCreditBalance()));
        }

        BigDecimal balanceBefore = customer.getCreditBalance();
        BigDecimal balanceAfter = balanceBefore.subtract(request.getAmount());

        customer.setCreditBalance(balanceAfter);
        customerRepository.save(customer);

        User currentUser = getCurrentUser();

        CreditTransaction transaction = CreditTransaction.builder()
                .customer(customer)
                .transactionType("PAYMENT_MADE")
                .amount(request.getAmount())
                .balanceBefore(balanceBefore)
                .balanceAfter(balanceAfter)
                .paymentMethod(request.getPaymentMethod())
                .notes(request.getNotes())
                .createdBy(currentUser)
                .build();

        transaction = creditTransactionRepository.save(transaction);

        Map<String, Object> response = new HashMap<>();
        response.put("transaction", mapToTransactionResponse(transaction));
        response.put("customer", mapToCustomerResponse(customer));

        return response;
    }

    @Transactional(readOnly = true)
    public List<CreditTransactionResponse> getCustomerTransactions(UUID customerId) {
        return creditTransactionRepository.findByCustomerId(customerId)
                .stream()
                .map(this::mapToTransactionResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<CreditTransactionResponse> getAllTransactions(Pageable pageable) {
        return creditTransactionRepository.findAll(pageable)
                .map(this::mapToTransactionResponse);
    }

    @Transactional(readOnly = true)
    public List<CustomerResponse> getOutstandingCreditAccounts() {
        return customerRepository.findCustomersWithCredit()
                .stream()
                .map(this::mapToCustomerResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalOutstandingCredit() {
        BigDecimal total = customerRepository.getTotalOutstandingCredit();
        return total != null ? total : BigDecimal.ZERO;
    }

    private User getCurrentUser() {
        UserPrincipal userPrincipal = (UserPrincipal) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        return userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private CreditTransactionResponse mapToTransactionResponse(CreditTransaction transaction) {
        return CreditTransactionResponse.builder()
                .id(transaction.getId())
                .customerId(transaction.getCustomer().getId())
                .customerName(transaction.getCustomer().getName())
                .saleId(transaction.getSale() != null ? transaction.getSale().getId() : null)
                .transactionType(transaction.getTransactionType())
                .amount(transaction.getAmount())
                .balanceBefore(transaction.getBalanceBefore())
                .balanceAfter(transaction.getBalanceAfter())
                .paymentMethod(transaction.getPaymentMethod())
                .notes(transaction.getNotes())
                .createdAt(transaction.getCreatedAt())
                .build();
    }

    private CustomerResponse mapToCustomerResponse(Customer customer) {
        return CustomerResponse.builder()
                .id(customer.getId())
                .name(customer.getName())
                .phone(customer.getPhone())
                .email(customer.getEmail())
                .address(customer.getAddress())
                .creditBalance(customer.getCreditBalance())
                .creditLimit(customer.getCreditLimit())
                .loyaltyPoints(customer.getLoyaltyPoints())
                .totalPurchases(customer.getTotalPurchases())
                .totalSpent(customer.getTotalSpent())
                .status(customer.getStatus())
                .createdAt(customer.getCreatedAt())
                .updatedAt(customer.getUpdatedAt())
                .build();
    }
}
