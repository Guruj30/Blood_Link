package com.bloodlink.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/**
 * Inbound request DTO for creating an emergency blood request.
 * Aligns with form fields in CreateRequestModal.jsx.
 */
public class EmergencyRequestCreateDto {

    @NotBlank(message = "Blood group is required")
    @Pattern(regexp = "^(A|B|AB|O)[+-]$", message = "Blood group must be one of: A+, A-, B+, B-, AB+, AB-, O+, O-")
    private String bloodGroup;

    @NotNull(message = "Units required is mandatory")
    @Min(value = 1, message = "At least 1 unit must be requested")
    @Max(value = 50, message = "Maximum 50 units allowed per single emergency request")
    private Integer units;

    @NotBlank(message = "Hospital / Requester facility name is required")
    @Size(max = 150, message = "Hospital name cannot exceed 150 characters")
    private String hospital;

    @NotBlank(message = "Location / City is required")
    @Size(max = 100, message = "Location cannot exceed 100 characters")
    private String location;

    @Pattern(regexp = "^(critical|high|normal|low)$", message = "Urgency must be: critical, high, normal, or low")
    private String urgency = "normal";

    @Size(max = 500, message = "Notes cannot exceed 500 characters")
    private String notes;

    @Size(max = 100, message = "Requester name cannot exceed 100 characters")
    private String requester = "Clinical Staff";

    public EmergencyRequestCreateDto() {
    }

    public EmergencyRequestCreateDto(String bloodGroup, Integer units, String hospital,
                                     String location, String urgency, String notes, String requester) {
        this.bloodGroup = bloodGroup;
        this.units = units;
        this.hospital = hospital;
        this.location = location;
        this.urgency = urgency != null ? urgency : "normal";
        this.notes = notes;
        this.requester = requester != null ? requester : "Clinical Staff";
    }

    public String getBloodGroup() {
        return bloodGroup;
    }

    public void setBloodGroup(String bloodGroup) {
        this.bloodGroup = bloodGroup;
    }

    public Integer getUnits() {
        return units;
    }

    public void setUnits(Integer units) {
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
}
