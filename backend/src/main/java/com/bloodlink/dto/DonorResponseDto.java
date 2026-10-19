package com.bloodlink.dto;

import java.time.LocalDate;

/**
 * Outbound response DTO for blood donor representation.
 * Maps 1:1 with frontend state items consumed in Donors.jsx and Manage.jsx.
 */
public class DonorResponseDto {

    private String id;
    private String name;
    private String bloodGroup;
    private String city;
    private String phone;
    private String availability;
    private LocalDate lastDonation;
    private LocalDate eligibleAfter;
    private LocalDate registered;
    private String notes;
    private boolean eligibleToDonate;
    private Long daysUntilEligible;

    public DonorResponseDto() {
    }

    public DonorResponseDto(String id, String name, String bloodGroup, String city,
                            String phone, String availability, LocalDate lastDonation,
                            LocalDate eligibleAfter, LocalDate registered, String notes,
                            boolean eligibleToDonate, Long daysUntilEligible) {
        this.id = id;
        this.name = name;
        this.bloodGroup = bloodGroup;
        this.city = city;
        this.phone = phone;
        this.availability = availability;
        this.lastDonation = lastDonation;
        this.eligibleAfter = eligibleAfter;
        this.registered = registered;
        this.notes = notes;
        this.eligibleToDonate = eligibleToDonate;
        this.daysUntilEligible = daysUntilEligible;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAvailability() {
        return availability;
    }

    public void setAvailability(String availability) {
        this.availability = availability;
    }

    public LocalDate getLastDonation() {
        return lastDonation;
    }

    public void setLastDonation(LocalDate lastDonation) {
        this.lastDonation = lastDonation;
    }

    public LocalDate getEligibleAfter() {
        return eligibleAfter;
    }

    public void setEligibleAfter(LocalDate eligibleAfter) {
        this.eligibleAfter = eligibleAfter;
    }

    public LocalDate getRegistered() {
        return registered;
    }

    public void setRegistered(LocalDate registered) {
        this.registered = registered;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public boolean isEligibleToDonate() {
        return eligibleToDonate;
    }

    public void setEligibleToDonate(boolean eligibleToDonate) {
        this.eligibleToDonate = eligibleToDonate;
    }

    public Long getDaysUntilEligible() {
        return daysUntilEligible;
    }

    public void setDaysUntilEligible(Long daysUntilEligible) {
        this.daysUntilEligible = daysUntilEligible;
    }
}
