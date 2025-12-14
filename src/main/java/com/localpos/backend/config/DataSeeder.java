package com.localpos.backend.config;

import com.localpos.backend.entity.Customer;
import com.localpos.backend.entity.Product;
import com.localpos.backend.entity.User;
import com.localpos.backend.repository.CustomerRepository;
import com.localpos.backend.repository.ProductRepository;
import com.localpos.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@Slf4j
@Profile("dev")
public class DataSeeder {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;

    @Bean
    public CommandLineRunner seedData() {
        return args -> {
            if (userRepository.count() == 0) {
                seedUsers();
            }
            if (productRepository.count() == 0) {
                seedProducts();
            }
            if (customerRepository.count() == 0) {
                seedCustomers();
            }
            log.info("✅ Test data seeding completed!");
        };
    }

    private void seedUsers() {
        User owner = User.builder()
                .email("owner@localpos.com")
                .passwordHash(passwordEncoder.encode("password123"))
                .fullName("Store Owner")
                .role("owner")
                .isActive(true)
                .build();

        User cashier = User.builder()
                .email("cashier@localpos.com")
                .passwordHash(passwordEncoder.encode("password123"))
                .fullName("Cashier User")
                .role("cashier")
                .isActive(true)
                .build();

        userRepository.saveAll(List.of(owner, cashier));
        log.info("✅ Seeded 2 users (owner@localpos.com / cashier@localpos.com - password: password123)");
    }

    private void seedProducts() {
        List<Product> products = new ArrayList<>();

        products.add(createProduct("Tata Salt 1kg", "Grocery", new BigDecimal("22"), 45, 20, "pcs", "8901234567890", null));
        products.add(createProduct("Maggi Noodles", "Instant Food", new BigDecimal("12"), 8, 20, "pcs", "8901234567891", null));
        products.add(createProduct("Parle-G Biscuits", "Snacks", new BigDecimal("5"), 12, 25, "pcs", "8901234567892", null));
        products.add(createProduct("Bru Coffee 50g", "Beverages", new BigDecimal("145"), 23, 10, "pcs", "8901234567893", null));
        products.add(createProduct("Colgate Toothpaste", "Personal Care", new BigDecimal("85"), 34, 15, "pcs", "8901234567894", null));
        products.add(createProduct("Lays Chips 50g", "Snacks", new BigDecimal("20"), 50, 30, "pcs", "8901234567895", null));
        products.add(createProduct("Amul Milk 500ml", "Dairy", new BigDecimal("28"), 15, 20, "pcs", "8901234567896", LocalDate.now().plusDays(2)));
        products.add(createProduct("Fresh Bread", "Bakery", new BigDecimal("40"), 10, 15, "pcs", "8901234567897", LocalDate.now().plusDays(1)));
        products.add(createProduct("Curd 200g", "Dairy", new BigDecimal("25"), 20, 15, "pcs", "8901234567898", LocalDate.now().plusDays(3)));
        products.add(createProduct("Sunflower Oil 1L", "Grocery", new BigDecimal("180"), 30, 10, "pcs", "8901234567899", null));
        products.add(createProduct("Basmati Rice 1kg", "Grocery", new BigDecimal("95"), 40, 20, "kg", "8901234567900", null));
        products.add(createProduct("Surf Excel 1kg", "Household", new BigDecimal("250"), 18, 10, "pcs", "8901234567901", null));
        products.add(createProduct("Red Label Tea 250g", "Beverages", new BigDecimal("120"), 25, 15, "pcs", "8901234567902", null));
        products.add(createProduct("Dairy Milk Chocolate", "Snacks", new BigDecimal("50"), 35, 20, "pcs", "8901234567903", null));
        products.add(createProduct("Vim Dishwash Bar", "Household", new BigDecimal("15"), 60, 30, "pcs", "8901234567904", null));

        productRepository.saveAll(products);
        log.info("✅ Seeded {} products", products.size());
    }

    private Product createProduct(String name, String category, BigDecimal price, int stock, int minStock, String unit, String barcode, LocalDate expiry) {
        return Product.builder()
                .name(name)
                .category(category)
                .price(price)
                .costPrice(price.multiply(new BigDecimal("0.7"))) // 70% of selling price
                .stockQuantity(stock)
                .minStockLevel(minStock)
                .unit(unit)
                .barcode(barcode)
                .expiryDate(expiry)
                .status("active")
                .build();
    }

    private void seedCustomers() {
        List<Customer> customers = new ArrayList<>();

        customers.add(createCustomer("Rajesh Kumar", "9876543210", "rajesh@example.com", new BigDecimal("450"), new BigDecimal("5000")));
        customers.add(createCustomer("Priya Sharma", "9876543211", "priya@example.com", new BigDecimal("0"), new BigDecimal("3000")));
        customers.add(createCustomer("Amit Patel", "9876543212", "amit@example.com", new BigDecimal("1200"), new BigDecimal("10000")));
        customers.add(createCustomer("Sunita Verma", "9876543213", "sunita@example.com", new BigDecimal("350"), new BigDecimal("4000")));
        customers.add(createCustomer("Vikram Singh", "9876543214", "vikram@example.com", new BigDecimal("0"), new BigDecimal("5000")));
        customers.add(createCustomer("Kavita Reddy", "9876543215", "kavita@example.com", new BigDecimal("890"), new BigDecimal("8000")));
        customers.add(createCustomer("Ramesh Gupta", "9876543216", "ramesh@example.com", new BigDecimal("0"), new BigDecimal("6000")));
        customers.add(createCustomer("Anjali Mehta", "9876543217", "anjali@example.com", new BigDecimal("250"), new BigDecimal("3500")));

        customerRepository.saveAll(customers);
        log.info("✅ Seeded {} customers", customers.size());
    }

    private Customer createCustomer(String name, String phone, String email, BigDecimal creditBalance, BigDecimal creditLimit) {
        return Customer.builder()
                .name(name)
                .phone(phone)
                .email(email)
                .creditBalance(creditBalance)
                .creditLimit(creditLimit)
                .loyaltyPoints(0)
                .totalPurchases(0)
                .totalSpent(BigDecimal.ZERO)
                .status("active")
                .build();
    }
}
