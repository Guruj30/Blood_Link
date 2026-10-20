package com.bloodlink.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

/**
 * Inbound request DTO for updating the status of an emergency request.
 * Supports status progression: pending -> reviewing -> in_progress -> fulfilled or cancelled.
 */
public class EmergencyRequestStatusUpdateDto {

    @NotBlank(message = "Status cannot be blank")
    @Pattern(regexp = "^(pending|reviewing|in_progress|fulfilled|cancelled)$",
            message = "Status must be: pending, reviewing, in_progress, fulfilled, or cancelled")
    private String status;

    private String notes;

    private boolean deductInventoryOnFulfillment = true;

    public EmergencyRequestStatusUpdateDto() {
    }

    public EmergencyRequestStatusUpdateDto(String status) {
        this.status = status;
    }

    public EmergencyRequestStatusUpdateDto(String status, String notes, boolean deductInventoryOnFulfillment) {
        this.status = status;
        this.notes = notes;
        this.deductInventoryOnFulfillment = deductInventoryOnFulfillment;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isDeductInventoryOnFulfillment() {
        return deductInventoryOnFulfillment;
    }

    public void setDeductInventoryOnFulfillment(boolean deductInventoryOnFulfillment) {
        this.deductInventoryOnFulfillment = deductInventoryOnFulfillment;
    }
}
