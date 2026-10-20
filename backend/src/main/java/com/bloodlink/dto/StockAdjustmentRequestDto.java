package com.bloodlink.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

/**
 * Inbound request DTO for adjusting blood inventory units and thresholds.
 * Supports set, add, and subtract operations matching AdjustStockModal in Inventory.jsx.
 */
public class StockAdjustmentRequestDto {

    @NotNull(message = "Quantity or units value is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    @Max(value = 9999, message = "Quantity exceeds allowable maximum")
    private Integer units;

    @Min(value = 1, message = "Low stock threshold must be at least 1")
    @Max(value = 500, message = "Threshold exceeds reasonable bounds")
    private Integer lowThreshold;

    @Pattern(regexp = "^(set|add|subtract)$", message = "Mode must be one of: set, add, subtract")
    private String mode = "set";

    public StockAdjustmentRequestDto() {
    }

    public StockAdjustmentRequestDto(Integer units, Integer lowThreshold, String mode) {
        this.units = units;
        this.lowThreshold = lowThreshold;
        this.mode = mode != null ? mode : "set";
    }

    public Integer getUnits() {
        return units;
    }

    public void setUnits(Integer units) {
        this.units = units;
    }

    public Integer getLowThreshold() {
        return lowThreshold;
    }

    public void setLowThreshold(Integer lowThreshold) {
        this.lowThreshold = lowThreshold;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }
}
