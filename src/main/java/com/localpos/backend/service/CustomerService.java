package com.localpos.backend.service;

import com.localpos.backend.dto.CustomerRequest;
import com.localpos.backend.dto.CustomerResponse;
import com.localpos.backend.entity.Customer;
import com.localpos.backend.exception.ResourceNotFoundException;
import com.localpos.backend.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository customerRepository;

    @Transactional(readOnly = true)
    public Page<CustomerResponse> getAllCustomers(String search, Pageable pageable) {
        Page<Customer> customers;

        if (search != null && !search.isEmpty()) {
            customers = customerRepository.findByNameContainingIgnoreCaseOrPhoneContaining(
                    search, search, pageable);
        } else {
            customers = customerRepository.findAll(pageable);
        }

        return customers.map(this::mapToCustomerResponse);
    }

    @Transactional(readOnly = true)
    public CustomerResponse getCustomerById(UUID id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));
        return mapToCustomerResponse(customer);
    }

    @Transactional
    public CustomerResponse createCustomer(CustomerRequest request) {
        Customer customer = Customer.builder()
                .name(request.getName())
                .phone(request.getPhone())
                .email(request.getEmail())
                .address(request.getAddress())
                .creditLimit(request.getCreditLimit())
                .status(request.getStatus())
                .build();

        customer = customerRepository.save(customer);
        return mapToCustomerResponse(customer);
    }

    @Transactional
    public CustomerResponse updateCustomer(UUID id, CustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        customer.setName(request.getName());
        customer.setPhone(request.getPhone());
        customer.setEmail(request.getEmail());
        customer.setAddress(request.getAddress());
        customer.setCreditLimit(request.getCreditLimit());
        customer.setStatus(request.getStatus());

        customer = customerRepository.save(customer);
        return mapToCustomerResponse(customer);
    }

    @Transactional
    public void deleteCustomer(UUID id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Customer not found with id: " + id));

        customer.setStatus("inactive");
        customerRepository.save(customer);
    }

    @Transactional(readOnly = true)
    public List<CustomerResponse> getCustomersWithCredit() {
        return customerRepository.findCustomersWithCredit()
                .stream()
                .map(this::mapToCustomerResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<CustomerResponse> getTopCustomers(Pageable pageable) {
        return customerRepository.findTopCustomers(pageable)
                .map(this::mapToCustomerResponse);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalOutstandingCredit() {
        BigDecimal total = customerRepository.getTotalOutstandingCredit();
        return total != null ? total : BigDecimal.ZERO;
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
