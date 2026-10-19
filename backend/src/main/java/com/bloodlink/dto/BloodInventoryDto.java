package com.bloodlink.dto;

import java.time.LocalDateTime;

/**
 * Representation of inventory item for a blood group.
 * Matches the inventory items in seedData.js and Inventory.jsx.
 */
public class BloodInventoryDto {

    private String id;
    private String group;
    private int units;
    private int lowThreshold;
    private LocalDateTime lastUpdated;
    private String status;
    private int stockPercent;

    public BloodInventoryDto() {
    }

    public BloodInventoryDto(String id, String group, int units, int lowThreshold,
                             LocalDateTime lastUpdated, String status, int stockPercent) {
        this.id = id;
        this.group = group;
        this.units = units;
        this.lowThreshold = lowThreshold;
        this.lastUpdated = lastUpdated;
        this.status = status;
        this.stockPercent = stockPercent;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        this.units = units;
    }

    public int getLowThreshold() {
        return lowThreshold;
    }

    public void setLowThreshold(int lowThreshold) {
        this.lowThreshold = lowThreshold;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getStockPercent() {
        return stockPercent;
    }

    public void setStockPercent(int stockPercent) {
        this.stockPercent = stockPercent;
    }
}
