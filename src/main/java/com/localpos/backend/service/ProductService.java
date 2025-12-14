package com.localpos.backend.service;

import com.localpos.backend.dto.ProductRequest;
import com.localpos.backend.dto.ProductResponse;
import com.localpos.backend.entity.Product;
import com.localpos.backend.entity.User;
import com.localpos.backend.exception.BadRequestException;
import com.localpos.backend.exception.ResourceNotFoundException;
import com.localpos.backend.repository.ProductRepository;
import com.localpos.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProducts(String search, Pageable pageable) {
        Page<Product> products;

        if (search != null && !search.isEmpty()) {
            products = productRepository.findByNameContainingIgnoreCaseOrCategoryContainingIgnoreCase(
                    search, search, pageable);
        } else {
            products = productRepository.findAll(pageable);
        }

        return products.map(this::mapToProductResponse);
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductById(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return mapToProductResponse(product);
    }

    @Transactional(readOnly = true)
    public ProductResponse getProductByBarcode(String barcode) {
        Product product = productRepository.findByBarcode(barcode)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with barcode: " + barcode));
        return mapToProductResponse(product);
    }

    @Transactional
    public ProductResponse createProduct(ProductRequest request, Authentication authentication) {
        if (request.getBarcode() != null && productRepository.existsByBarcode(request.getBarcode())) {
            throw new BadRequestException("Product with barcode already exists");
        }
        String userEmail= authentication.getName();
        System.out.println("User id "+userEmail);

        User user1=userRepository.findByEmail(userEmail).orElse(null);

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .category(request.getCategory())
                .price(request.getPrice())
                .costPrice(request.getCostPrice())
                .stockQuantity(request.getStockQuantity())
                .minStockLevel(request.getMinStockLevel())
                .unit(request.getUnit())
                .barcode(request.getBarcode())
                .expiryDate(request.getExpiryDate())
                .status(request.getStatus())
                .build();

        product.setCreatedBy(user1);
        product = productRepository.save(product);
        return mapToProductResponse(product);
    }

    @Transactional
    public ProductResponse updateProduct(UUID id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        if (request.getBarcode() != null && !request.getBarcode().equals(product.getBarcode())
                && productRepository.existsByBarcode(request.getBarcode())) {
            throw new BadRequestException("Product with barcode already exists");
        }

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setCategory(request.getCategory());
        product.setPrice(request.getPrice());
        product.setCostPrice(request.getCostPrice());
        product.setStockQuantity(request.getStockQuantity());
        product.setMinStockLevel(request.getMinStockLevel());
        product.setUnit(request.getUnit());
        product.setBarcode(request.getBarcode());
        product.setExpiryDate(request.getExpiryDate());
        product.setStatus(request.getStatus());

        product = productRepository.save(product);
        return mapToProductResponse(product);
    }

    @Transactional
    public void deleteProduct(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        product.setStatus("inactive");
        productRepository.save(product);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getLowStockProducts() {
        return productRepository.findLowStockProducts()
                .stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getExpiringProducts() {
        LocalDate sevenDaysFromNow = LocalDate.now().plusDays(7);
        return productRepository.findExpiringProducts(sevenDaysFromNow)
                .stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
    }

    private ProductResponse mapToProductResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .category(product.getCategory())
                .price(product.getPrice())
                .costPrice(product.getCostPrice())
                .stockQuantity(product.getStockQuantity())
                .minStockLevel(product.getMinStockLevel())
                .unit(product.getUnit())
                .barcode(product.getBarcode())
                .expiryDate(product.getExpiryDate())
                .status(product.getStatus())
                .isLowStock(product.isLowStock())
                .isExpiringSoon(product.isExpiringSoon())
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }
}
