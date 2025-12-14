package com.localpos.backend.controller;

import com.localpos.backend.dto.ApiResponse;
import com.localpos.backend.dto.StoreSettingRequest;
import com.localpos.backend.dto.StoreSettingResponse;
import com.localpos.backend.service.StoreSettingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/settings")
@RequiredArgsConstructor
public class StoreSettingController {

    private final StoreSettingService storeSettingService;

    @GetMapping
    public ResponseEntity<ApiResponse<StoreSettingResponse>> getSettings() {
        StoreSettingResponse settings = storeSettingService.getSettings();
        return ResponseEntity.ok(ApiResponse.success(settings));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<StoreSettingResponse>> updateSettings(
            @PathVariable UUID id,
            @Valid @RequestBody StoreSettingRequest request) {
        StoreSettingResponse settings = storeSettingService.updateSettings(id, request);
        return ResponseEntity.ok(ApiResponse.success("Settings updated successfully", settings));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<StoreSettingResponse>> createSettings(
            @Valid @RequestBody StoreSettingRequest request) {
        StoreSettingResponse settings = storeSettingService.createSettings(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Settings created successfully", settings));
    }
}
