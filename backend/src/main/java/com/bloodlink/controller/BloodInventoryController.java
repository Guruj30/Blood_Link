package com.bloodlink.controller;

import com.bloodlink.dto.BloodInventoryDto;
import com.bloodlink.dto.StockAdjustmentRequestDto;
import com.bloodlink.service.BloodInventoryService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for blood inventory operations.
 * Consumed by Inventory.jsx and AdjustStockModal.
 */
@RestController
@RequestMapping("/api/inventory")
public class BloodInventoryController {

    private final BloodInventoryService inventoryService;

    public BloodInventoryController(BloodInventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @GetMapping
    public ResponseEntity<List<BloodInventoryDto>> getAllInventory() {
        return ResponseEntity.ok(inventoryService.getAllInventory());
    }

    @GetMapping("/{bloodGroup}")
    public ResponseEntity<BloodInventoryDto> getInventoryByGroup(@PathVariable String bloodGroup) {
        return ResponseEntity.ok(inventoryService.getInventoryByGroup(bloodGroup));
    }

    @PutMapping("/{bloodGroup}/adjust")
    public ResponseEntity<BloodInventoryDto> adjustInventory(
            @PathVariable String bloodGroup,
            @Valid @RequestBody StockAdjustmentRequestDto dto) {
        BloodInventoryDto updated = inventoryService.adjustInventory(bloodGroup, dto);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/{bloodGroup}")
    public ResponseEntity<BloodInventoryDto> updateInventory(
            @PathVariable String bloodGroup,
            @Valid @RequestBody StockAdjustmentRequestDto dto) {
        return adjustInventory(bloodGroup, dto);
    }
}
