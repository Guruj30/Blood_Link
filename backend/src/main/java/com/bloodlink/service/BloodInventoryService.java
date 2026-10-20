package com.bloodlink.service;

import com.bloodlink.dto.BloodInventoryDto;
import com.bloodlink.dto.StockAdjustmentRequestDto;
import com.bloodlink.exception.ExpiredBloodUnitException;
import com.bloodlink.exception.InsufficientStockException;
import com.bloodlink.exception.InvalidBloodGroupException;
import com.bloodlink.exception.ResourceNotFoundException;
import com.bloodlink.model.BloodGroup;
import com.bloodlink.model.BloodInventoryItem;
import com.bloodlink.model.BloodUnit;
import com.bloodlink.repository.BloodInventoryRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Business service handling blood inventory tracking, stock adjustments,
 * and safety validations (preventing negative stock or expired unit distribution).
 */
@Service
public class BloodInventoryService {

    private final BloodInventoryRepository inventoryRepository;

    public BloodInventoryService(BloodInventoryRepository inventoryRepository) {
        this.inventoryRepository = inventoryRepository;
    }

    public List<BloodInventoryDto> getAllInventory() {
        return inventoryRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public BloodInventoryDto getInventoryByGroup(String bloodGroupStr) {
        BloodGroup group = parseBloodGroup(bloodGroupStr);
        BloodInventoryItem item = inventoryRepository.findByBloodGroup(group)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory for group", group.getCode()));
        return toDto(item);
    }

    /**
     * Concurrency-safe stock adjustment supporting set, add, and subtract operations.
     */
    public synchronized BloodInventoryDto adjustInventory(String bloodGroupStr, StockAdjustmentRequestDto dto) {
        BloodGroup group = parseBloodGroup(bloodGroupStr);
        BloodInventoryItem item = inventoryRepository.findByBloodGroup(group)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory for group", group.getCode()));

        String mode = dto.getMode() != null ? dto.getMode().trim().toLowerCase() : "set";
        int amount = dto.getUnits() != null ? dto.getUnits() : 0;

        switch (mode) {
            case "add" -> item.addUnits(amount);
            case "subtract" -> {
                if (item.getUnits() < amount) {
                    throw new InsufficientStockException(group.getCode(), amount, item.getUnits());
                }
                item.deductUnits(amount);
            }
            case "set" -> item.setUnits(amount);
            default -> throw new IllegalArgumentException("Unsupported adjustment mode: " + mode);
        }

        if (dto.getLowThreshold() != null && dto.getLowThreshold() > 0) {
            item.setLowThreshold(dto.getLowThreshold());
        }

        BloodInventoryItem saved = inventoryRepository.save(item);
        return toDto(saved);
    }

    /**
     * Atomically deducts blood units from inventory, throwing InsufficientStockException if unavailable.
     */
    public synchronized void deductStock(BloodGroup group, int units) {
        if (units <= 0) return;
        BloodInventoryItem item = inventoryRepository.findByBloodGroup(group)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory for group", group.getCode()));

        if (item.getUnits() < units) {
            throw new InsufficientStockException(group.getCode(), units, item.getUnits());
        }
        item.deductUnits(units);
        inventoryRepository.save(item);
    }

    public boolean hasSufficientStock(BloodGroup group, int units) {
        return inventoryRepository.findByBloodGroup(group)
                .map(item -> item.getUnits() >= units)
                .orElse(false);
    }

    public int getAvailableStock(BloodGroup group) {
        return inventoryRepository.findByBloodGroup(group)
                .map(BloodInventoryItem::getUnits)
                .orElse(0);
    }

    /**
     * Safety validation: rejects expired blood units.
     */
    public void validateUnitNotExpired(BloodUnit unit) {
        if (unit == null) return;
        if (unit.isExpired()) {
            throw new ExpiredBloodUnitException(String.format(
                    "Safety Violation: Blood unit %s (%s) expired on %s and cannot be allocated for clinical transfusion.",
                    unit.getUnitId(), unit.getBloodGroup().getCode(), unit.getExpirationDate()
            ));
        }
    }

    private BloodGroup parseBloodGroup(String bgStr) {
        try {
            return BloodGroup.fromCode(bgStr);
        } catch (IllegalArgumentException e) {
            throw new InvalidBloodGroupException(bgStr);
        }
    }

    public BloodInventoryDto toDto(BloodInventoryItem item) {
        int percent = Math.min(100, Math.round(((float) item.getUnits() / 80.0f) * 100));
        return new BloodInventoryDto(
                item.getId(),
                item.getBloodGroup().getCode(),
                item.getUnits(),
                item.getLowThreshold(),
                item.getLastUpdated(),
                item.getStatus(),
                percent
        );
    }
}
