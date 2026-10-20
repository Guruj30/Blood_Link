package com.bloodlink.dto;

import java.util.List;

/**
 * Result DTO for blood component compatibility matching.
 * Includes explicit medical disclaimers as required by healthcare safety guidelines.
 */
public class CompatibilityCheckResponseDto {

    private String recipientBloodGroup;
    private String donorBloodGroup;
    private boolean compatible;
    private String componentType;
    private List<String> allCompatibleDonorsForRecipient;
    private String clinicalExplanation;
    private String medicalDisclaimer;

    public CompatibilityCheckResponseDto() {
    }

    public CompatibilityCheckResponseDto(String recipientBloodGroup, String donorBloodGroup,
                                         boolean compatible, String componentType,
                                         List<String> allCompatibleDonorsForRecipient,
                                         String clinicalExplanation, String medicalDisclaimer) {
        this.recipientBloodGroup = recipientBloodGroup;
        this.donorBloodGroup = donorBloodGroup;
        this.compatible = compatible;
        this.componentType = componentType;
        this.allCompatibleDonorsForRecipient = allCompatibleDonorsForRecipient;
        this.clinicalExplanation = clinicalExplanation;
        this.medicalDisclaimer = medicalDisclaimer;
    }

    public String getRecipientBloodGroup() {
        return recipientBloodGroup;
    }

    public void setRecipientBloodGroup(String recipientBloodGroup) {
        this.recipientBloodGroup = recipientBloodGroup;
    }

    public String getDonorBloodGroup() {
        return donorBloodGroup;
    }

    public void setDonorBloodGroup(String donorBloodGroup) {
        this.donorBloodGroup = donorBloodGroup;
    }

    public boolean isCompatible() {
        return compatible;
    }

    public void setCompatible(boolean compatible) {
        this.compatible = compatible;
    }

    public String getComponentType() {
        return componentType;
    }

    public void setComponentType(String componentType) {
        this.componentType = componentType;
    }

    public List<String> getAllCompatibleDonorsForRecipient() {
        return allCompatibleDonorsForRecipient;
    }

    public void setAllCompatibleDonorsForRecipient(List<String> allCompatibleDonorsForRecipient) {
        this.allCompatibleDonorsForRecipient = allCompatibleDonorsForRecipient;
    }

    public String getClinicalExplanation() {
        return clinicalExplanation;
    }

    public void setClinicalExplanation(String clinicalExplanation) {
        this.clinicalExplanation = clinicalExplanation;
    }

    public String getMedicalDisclaimer() {
        return medicalDisclaimer;
    }

    public void setMedicalDisclaimer(String medicalDisclaimer) {
        this.medicalDisclaimer = medicalDisclaimer;
    }
}
