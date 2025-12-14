package com.localpos.backend.service;

import com.localpos.backend.dto.StoreSettingRequest;
import com.localpos.backend.dto.StoreSettingResponse;
import com.localpos.backend.entity.StoreSetting;
import com.localpos.backend.exception.ResourceNotFoundException;
import com.localpos.backend.repository.StoreSettingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StoreSettingService {

    private final StoreSettingRepository storeSettingRepository;

    @Transactional(readOnly = true)
    public StoreSettingResponse getSettings() {
        StoreSetting settings = storeSettingRepository.findLatest()
                .orElseGet(() -> createDefaultSettings());

        return mapToResponse(settings);
    }

    @Transactional
    public StoreSettingResponse updateSettings(UUID id, StoreSettingRequest request) {
        StoreSetting settings = storeSettingRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Settings not found"));

        settings.setStoreName(request.getStoreName());
        settings.setContactNumber(request.getContactNumber());
        settings.setAddress(request.getAddress());
        settings.setCurrencySymbol(request.getCurrencySymbol());
        settings.setTaxRate(request.getTaxRate());
        settings.setMinStockDefault(request.getMinStockDefault());
        settings.setReceiptHeader(request.getReceiptHeader());
        settings.setReceiptFooter(request.getReceiptFooter());

        settings = storeSettingRepository.save(settings);
        return mapToResponse(settings);
    }

    @Transactional
    public StoreSettingResponse createSettings(StoreSettingRequest request) {
        StoreSetting settings = StoreSetting.builder()
                .storeName(request.getStoreName())
                .contactNumber(request.getContactNumber())
                .address(request.getAddress())
                .currencySymbol(request.getCurrencySymbol())
                .taxRate(request.getTaxRate())
                .minStockDefault(request.getMinStockDefault())
                .receiptHeader(request.getReceiptHeader())
                .receiptFooter(request.getReceiptFooter())
                .build();

        settings = storeSettingRepository.save(settings);
        return mapToResponse(settings);
    }

    private StoreSetting createDefaultSettings() {
        StoreSetting defaultSettings = StoreSetting.builder()
                .storeName("My Kirana Store")
                .currencySymbol("₹")
                .taxRate(java.math.BigDecimal.valueOf(5.00))
                .minStockDefault(10)
                .receiptHeader("Thank you for shopping with us!")
                .receiptFooter("Visit again!")
                .build();

        return storeSettingRepository.save(defaultSettings);
    }

    private StoreSettingResponse mapToResponse(StoreSetting settings) {
        return StoreSettingResponse.builder()
                .id(settings.getId())
                .storeName(settings.getStoreName())
                .contactNumber(settings.getContactNumber())
                .address(settings.getAddress())
                .currencySymbol(settings.getCurrencySymbol())
                .taxRate(settings.getTaxRate())
                .minStockDefault(settings.getMinStockDefault())
                .receiptHeader(settings.getReceiptHeader())
                .receiptFooter(settings.getReceiptFooter())
                .createdAt(settings.getCreatedAt())
                .updatedAt(settings.getUpdatedAt())
                .build();
    }
}
