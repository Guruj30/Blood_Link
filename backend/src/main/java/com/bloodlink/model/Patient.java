package com.bloodlink.model;

import java.util.Objects;

/**
 * Domain entity representing a patient receiving emergency transfusion support.
 * Demonstrates OOP Inheritance from Person alongside Donor.
 */
public class Patient extends Person {

    private BloodGroup bloodGroup;
    private String hospital;
    private String medicalRecordNumber;

    public Patient() {
        super();
    }

    public Patient(String id, String name, String phone, String city,
                   BloodGroup bloodGroup, String hospital, String medicalRecordNumber) {
        super(id, name, phone, city);
        this.bloodGroup = Objects.requireNonNull(bloodGroup, "Blood group must not be null");
        this.hospital = hospital;
        this.medicalRecordNumber = medicalRecordNumber;
    }

    public BloodGroup getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(BloodGroup bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public String getHospital() {
        return hospital;
    }

    public void setHospital(String hospital) {
        this.hospital = hospital;
    }

    public String getMedicalRecordNumber() {
        return medicalRecordNumber;
    }

    public void setMedicalRecordNumber(String medicalRecordNumber) {
        this.medicalRecordNumber = medicalRecordNumber;
    }

    @Override
    public String getRoleDescription() {
        return "Transfusion Recipient / Patient at " + (hospital != null ? hospital : "Hospital");
    }
}
