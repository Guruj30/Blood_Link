package com.bloodlink.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Outbound response DTO for emergency requests.
 * Fully compatible with frontend representation in Requests.jsx and RequestDetailModal.jsx.
 */
public class EmergencyRequestResponseDto {

    private String id;
    private String bloodGroup;
    private int units;
    private String hospital;
    private String location;
    private String urgency;
    private String status;
    private LocalDateTime createdAt;
    private String notes;
    private String requester;
    private List<String> compatibleBloodGroups;
    private int currentStockForGroup;
    private boolean canFulfillFromStock;

    public EmergencyRequestResponseDto() {
    }

    public EmergencyRequestResponseDto(String id, String bloodGroup, int units, String hospital,
                                       String location, String urgency, String status,
                                       LocalDateTime createdAt, String notes, String requester,
                                       List<String> compatibleBloodGroups, int currentStockForGroup,
                                       boolean canFulfillFromStock) {
        this.id = id;
        this.bloodGroup = bloodGroup;
        this.units = units;
        this.hospital = hospital;
        this.location = location;
        this.urgency = urgency;
        this.status = status;
        this.createdAt = createdAt;
        this.notes = notes;
        this.requester = requester;
        this.compatibleBloodGroups = compatibleBloodGroups;
        this.currentStockForGroup = currentStockForGroup;
        this.canFulfillFromStock = canFulfillFromStock;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public int getUnits() {
        return units;
    }

    public void setUnits(int units) {
        this.units = units;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getUrgency() {
        return urgency;
    }

    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getRequester() {
        return requester;
    }

    public void setRequester(String requester) {
        this.requester = requester;
    }

    public List<String> getCompatibleBloodGroups() {
        return compatibleBloodGroups;
    }

    public void setCompatibleBloodGroups(List<String> compatibleBloodGroups) {
        this.compatibleBloodGroups = compatibleBloodGroups;
    }

    public int getCurrentStockForGroup() {
        return currentStockForGroup;
    }

    public void setCurrentStockForGroup(int currentStockForGroup) {
        this.currentStockForGroup = currentStockForGroup;
    }

    public boolean isCanFulfillFromStock() {
        return canFulfillFromStock;
    }

    public void setCanFulfillFromStock(boolean canFulfillFromStock) {
        this.canFulfillFromStock = canFulfillFromStock;
    }
}
