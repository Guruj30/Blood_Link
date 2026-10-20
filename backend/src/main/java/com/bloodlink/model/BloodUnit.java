package com.bloodlink.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents an individual physical unit/bag of blood in clinical inventory.
 * Encapsulates shelf-life, collection, and expiration validation.
 * Note: Standard refrigerated red-cell shelf-life is typically 35-42 days.
 */
public class BloodUnit {

    private String unitId;
    private BloodGroup bloodGroup;
    private LocalDate collectionDate;
    private LocalDate expirationDate;
    private boolean reserved;
    private boolean disposed;

    public BloodUnit() {
    }

    public BloodUnit(String unitId, BloodGroup bloodGroup, LocalDate collectionDate, int shelfLifeDays) {
        this.unitId = Objects.requireNonNull(unitId, "Unit ID must not be null");
        this.bloodGroup = Objects.requireNonNull(bloodGroup, "Blood group must not be null");
        this.collectionDate = Objects.requireNonNull(collectionDate, "Collection date must not be null");
        this.expirationDate = collectionDate.plusDays(shelfLifeDays);
        this.reserved = false;
        this.disposed = false;
    }

    public String getUnitId() {
        return unitId;
    }

    public BloodGroup getBloodGroup() {
        return bloodGroup;
    }

    public LocalDate getCollectionDate() {
        return collectionDate;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public boolean isReserved() {
        return reserved;
    }

    public void setReserved(boolean reserved) {
        this.reserved = reserved;
    }

    public boolean isDisposed() {
        return disposed;
    }

    public void setDisposed(boolean disposed) {
        this.disposed = disposed;
    }

    /**
     * Business validation checking if the blood unit is expired.
     */
    public boolean isExpired() {
        return isExpiredAsOf(LocalDate.now());
    }

    public boolean isExpiredAsOf(LocalDate date) {
        return expirationDate != null && expirationDate.isBefore(date);
    }

    /**
     * Checks if this unit is safe and available for allocation.
     */
    public boolean isUsable() {
        return !disposed && !reserved && !isExpired();
    }
}
