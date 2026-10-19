package com.bloodlink.exception;

/**
 * Thrown when an inventory reduction is attempted on insufficient blood stock.
 */
public class InsufficientStockException extends BloodLinkException {

    private final String bloodGroup;
    private final int requested;
    private final int available;

    public InsufficientStockException(String bloodGroup, int requested, int available) {
        super(String.format("Insufficient stock for group %s: requested %d units, but only %d available",
                bloodGroup, requested, available));
        this.bloodGroup = bloodGroup;
        this.requested = requested;
        this.available = available;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public int getRequested() {
        return requested;
    }

    public int getAvailable() {
        return available;
    }
}
