package com.bloodlink.dto;

import java.util.List;

/**
 * High-level clinical KPIs for the Overview / Dashboard page.
 * Aligns with selectors in AppStore.jsx and metrics in Overview.jsx.
 */
public class DashboardSummaryDto {

    private int totalDonors;
    private int availableDonors;
    private int totalUnits;
    private int activeRequests;
    private int fulfilledRequests;
    private int lowStockCount;
    private int criticalStockCount;
    private List<EmergencyRequestResponseDto> recentRequests;
    private List<BloodInventoryDto> lowStockAlerts;

    public DashboardSummaryDto() {
    }

    public DashboardSummaryDto(int totalDonors, int availableDonors, int totalUnits,
                               int activeRequests, int fulfilledRequests, int lowStockCount,
                               int criticalStockCount, List<EmergencyRequestResponseDto> recentRequests,
                               List<BloodInventoryDto> lowStockAlerts) {
        this.totalDonors = totalDonors;
        this.availableDonors = availableDonors;
        this.totalUnits = totalUnits;
        this.activeRequests = activeRequests;
        this.fulfilledRequests = fulfilledRequests;
        this.lowStockCount = lowStockCount;
        this.criticalStockCount = criticalStockCount;
        this.recentRequests = recentRequests;
        this.lowStockAlerts = lowStockAlerts;
    }

    public int getTotalDonors() {
        return totalDonors;
    }

    public void setTotalDonors(int totalDonors) {
        this.totalDonors = totalDonors;
    }

    public int getAvailableDonors() {
        return availableDonors;
    }

    public void setAvailableDonors(int availableDonors) {
        this.availableDonors = availableDonors;
    }

    public int getTotalUnits() {
        return totalUnits;
    }

    public void setTotalUnits(int totalUnits) {
        this.totalUnits = totalUnits;
    }

    public int getActiveRequests() {
        return activeRequests;
    }

    public void setActiveRequests(int activeRequests) {
        this.activeRequests = activeRequests;
    }

    public int getFulfilledRequests() {
        return fulfilledRequests;
    }

    public void setFulfilledRequests(int fulfilledRequests) {
        this.fulfilledRequests = fulfilledRequests;
    }

    public int getLowStockCount() {
        return lowStockCount;
    }

    public void setLowStockCount(int lowStockCount) {
        this.lowStockCount = lowStockCount;
    }

    public int getCriticalStockCount() {
        return criticalStockCount;
    }

    public void setCriticalStockCount(int criticalStockCount) {
        this.criticalStockCount = criticalStockCount;
    }

    public List<EmergencyRequestResponseDto> getRecentRequests() {
        return recentRequests;
    }

    public void setRecentRequests(List<EmergencyRequestResponseDto> recentRequests) {
        this.recentRequests = recentRequests;
    }

    public List<BloodInventoryDto> getLowStockAlerts() {
        return lowStockAlerts;
    }

    public void setLowStockAlerts(List<BloodInventoryDto> lowStockAlerts) {
        this.lowStockAlerts = lowStockAlerts;
    }
}
