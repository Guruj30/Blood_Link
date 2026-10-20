package com.bloodlink.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Domain entity representing a voluntary blood donor in the BloodLink platform.
 * Inherits common person traits from Person and encapsulates donation history,
 * blood classification, and eligibility calculation.
 */
public class Donor extends Person {

    private BloodGroup bloodGroup;
    private AvailabilityStatus availability;
    private LocalDate lastDonation;
    private LocalDate eligibleAfter;
    private LocalDate registered;
    private String notes;

    public Donor() {
        super();
        this.availability = AvailabilityStatus.AVAILABLE;
        this.registered = LocalDate.now();
    }

    public Donor(String id, String name, BloodGroup bloodGroup, String city,
                 String phone, AvailabilityStatus availability,
                 LocalDate lastDonation, LocalDate eligibleAfter,
                 LocalDate registered, String notes) {
        super(id, name, phone, city);
        this.bloodGroup = Objects.requireNonNull(bloodGroup, "Blood group must not be null");
        this.availability = availability != null ? availability : AvailabilityStatus.AVAILABLE;
        this.lastDonation = lastDonation;
        this.eligibleAfter = eligibleAfter;
        this.registered = registered != null ? registered : LocalDate.now();
        this.notes = notes;
    }

    public BloodGroup getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(BloodGroup bloodGroup) {
        this.bloodGroup = Objects.requireNonNull(bloodGroup, "Blood group must not be null");
    }

    public AvailabilityStatus getAvailability() {
        return availability;
    }

    public void setAvailability(AvailabilityStatus availability) {
        this.availability = availability != null ? availability : AvailabilityStatus.AVAILABLE;
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

    /**
     * Records a new donation event and recalculates the eligible-after date.
     * Encapsulates domain logic directly on the entity.
     *
     * @param donationDate date of whole blood donation
     * @param intervalDays minimum rest interval in days (default: 90)
     */
    public void recordDonation(LocalDate donationDate, int intervalDays) {
        if (donationDate == null) {
            throw new IllegalArgumentException("Donation date must not be null");
        }
        if (donationDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("Donation date cannot be in the future");
        }
        this.lastDonation = donationDate;
        this.eligibleAfter = donationDate.plusDays(intervalDays);
        this.availability = AvailabilityStatus.UNAVAILABLE;
    }

    @Override
    public String getRoleDescription() {
        return "Voluntary Blood Donor [" + bloodGroup.getCode() + "]";
    }
}
