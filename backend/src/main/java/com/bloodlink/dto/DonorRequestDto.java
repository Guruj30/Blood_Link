package com.bloodlink.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

/**
 * Inbound request DTO for registering or updating a blood donor.
 * Fields precisely align with the frontend AddDonorModal and Manage forms.
 */
public class DonorRequestDto {

    @NotBlank(message = "Donor full name is required")
    @Size(max = 100, message = "Name cannot exceed 100 characters")
    private String name;

    @NotBlank(message = "Blood group is required")
    @Pattern(regexp = "^(A|B|AB|O)[+-]$", message = "Blood group must be one of: A+, A-, B+, B-, AB+, AB-, O+, O-")
    private String bloodGroup;

    @NotBlank(message = "City / Area is required")
    @Size(max = 100, message = "City cannot exceed 100 characters")
    private String city;

    @Size(max = 25, message = "Phone cannot exceed 25 characters")
    private String phone;

    @Pattern(regexp = "^(available|unavailable|on_leave)$", message = "Availability must be: available, unavailable, or on_leave")
    private String availability = "available";

    private LocalDate lastDonation;

    @Size(max = 250, message = "Notes cannot exceed 250 characters")
    private String notes;

    public DonorRequestDto() {
    }

    public DonorRequestDto(String name, String bloodGroup, String city, String phone,
                           String availability, LocalDate lastDonation, String notes) {
        this.name = name;
        this.bloodGroup = bloodGroup;
        this.city = city;
        this.phone = phone;
        this.availability = availability != null ? availability : "available";
        this.lastDonation = lastDonation;
        this.notes = notes;
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

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
