package com.bloodlink.dto;

import java.util.List;
import java.util.Map;

/**
 * Analytical reports summary DTO matching data requirements in Reports.jsx.
 */
public class ReportsSummaryDto {

    private List<BloodInventoryDto> inventoryDistribution;
    private Map<String, Long> requestStatusBreakdown;
    private Map<String, Long> urgencyBreakdown;
    private Map<String, Long> donorAvailabilityBreakdown;
    private Map<String, Long> donorBloodGroupBreakdown;

    public ReportsSummaryDto() {
    }

    public ReportsSummaryDto(List<BloodInventoryDto> inventoryDistribution,
                             Map<String, Long> requestStatusBreakdown,
                             Map<String, Long> urgencyBreakdown,
                             Map<String, Long> donorAvailabilityBreakdown,
                             Map<String, Long> donorBloodGroupBreakdown) {
        this.inventoryDistribution = inventoryDistribution;
        this.requestStatusBreakdown = requestStatusBreakdown;
        this.urgencyBreakdown = urgencyBreakdown;
        this.donorAvailabilityBreakdown = donorAvailabilityBreakdown;
        this.donorBloodGroupBreakdown = donorBloodGroupBreakdown;
    }

    public List<BloodInventoryDto> getInventoryDistribution() {
        return inventoryDistribution;
    }

    public void setInventoryDistribution(List<BloodInventoryDto> inventoryDistribution) {
        this.inventoryDistribution = inventoryDistribution;
    }

    public Map<String, Long> getRequestStatusBreakdown() {
        return requestStatusBreakdown;
    }

    public void setRequestStatusBreakdown(Map<String, Long> requestStatusBreakdown) {
        this.requestStatusBreakdown = requestStatusBreakdown;
    }

    public Map<String, Long> getUrgencyBreakdown() {
        return urgencyBreakdown;
    }

    public void setUrgencyBreakdown(Map<String, Long> urgencyBreakdown) {
        this.urgencyBreakdown = urgencyBreakdown;
    }

    public Map<String, Long> getDonorAvailabilityBreakdown() {
        return donorAvailabilityBreakdown;
    }

    public void setDonorAvailabilityBreakdown(Map<String, Long> donorAvailabilityBreakdown) {
        this.donorAvailabilityBreakdown = donorAvailabilityBreakdown;
    }

    public Map<String, Long> getDonorBloodGroupBreakdown() {
        return donorBloodGroupBreakdown;
    }

    public void setDonorBloodGroupBreakdown(Map<String, Long> donorBloodGroupBreakdown) {
        this.donorBloodGroupBreakdown = donorBloodGroupBreakdown;
    }
}
