package com.bloodlink.model;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Encapsulates aggregated blood inventory for a specific blood group.
 * Matches the frontend inventory matrix (id, group, units, lowThreshold, lastUpdated).
 * Provides thread-safe / atomic-friendly business operations for stock adjustments.
 */
public class BloodInventoryItem {

    private String id;
    private BloodGroup bloodGroup;
    private int units;
    private int lowThreshold;
    private LocalDateTime lastUpdated;

    public BloodInventoryItem() {
    }

    public BloodInventoryItem(String id, BloodGroup bloodGroup, int units, int lowThreshold, LocalDateTime lastUpdated) {
        this.id = id;
        this.bloodGroup = Objects.requireNonNull(bloodGroup, "Blood group must not be null");
        this.units = Math.max(0, units);
        this.lowThreshold = Math.max(1, lowThreshold);
        this.lastUpdated = lastUpdated != null ? lastUpdated : LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public BloodGroup getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(BloodGroup bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public synchronized int getUnits() {
        return units;
    }

    public synchronized void setUnits(int units) {
        if (units < 0) {
            throw new IllegalArgumentException("Units cannot be negative");
        }
        this.units = units;
        this.lastUpdated = LocalDateTime.now();
    }

    public synchronized int getLowThreshold() {
        return lowThreshold;
    }

    public synchronized void setLowThreshold(int lowThreshold) {
        if (lowThreshold < 1) {
            throw new IllegalArgumentException("Low stock threshold must be at least 1");
        }
        this.lowThreshold = lowThreshold;
        this.lastUpdated = LocalDateTime.now();
    }

    public synchronized LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public synchronized void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    /**
     * Atomically adds units to the inventory item.
     */
    public synchronized int addUnits(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity to add cannot be negative");
        }
        this.units += quantity;
        this.lastUpdated = LocalDateTime.now();
        return this.units;
    }

    /**
     * Atomically deducts units from the inventory item, preventing negative stock.
     */
    public synchronized int deductUnits(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity to deduct cannot be negative");
        }
        if (this.units < quantity) {
            throw new IllegalStateException("Insufficient stock for group " + bloodGroup.getCode() +
                    ": available " + this.units + ", requested " + quantity);
        }
        this.units -= quantity;
        this.lastUpdated = LocalDateTime.now();
        return this.units;
    }

    /**
     * Determines status category corresponding to frontend definitions:
     * - empty: 0 units
     * - critical: <= 50% of lowThreshold
     * - low: <= lowThreshold
     * - good: > lowThreshold
     */
    public synchronized String getStatus() {
        if (units == 0) return "empty";
        if (units <= Math.floor(lowThreshold * 0.5)) return "critical";
        if (units <= lowThreshold) return "low";
        return "good";
    }
}
