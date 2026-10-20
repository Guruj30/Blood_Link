package com.bloodlink.service;

import com.bloodlink.model.BloodGroup;

import java.util.List;

/**
 * Abstraction for blood component compatibility matching engines.
 * Enables polymorphic rule evaluation across different clinical transfusion components
 * (such as packed red blood cells vs fresh frozen plasma).
 */
public interface CompatibilityRuleEngine {

    String MEDICAL_DISCLAIMER = "Software matching is an operational decision support tool only. " +
            "It does NOT replace clinical crossmatching, antibody screening, or professional medical assessment and physician approval.";

    /**
     * Component type evaluated by this engine (e.g. RED_CELL, PLASMA).
     */
    String getComponentType();

    /**
     * Determines whether donor blood is immunologically compatible with the recipient.
     */
    boolean isCompatible(BloodGroup donor, BloodGroup recipient);

    /**
     * Retrieves all donor blood groups compatible with the specified recipient.
     */
    List<BloodGroup> getCompatibleDonorsForRecipient(BloodGroup recipient);

    /**
     * Retrieves all recipient blood groups that can receive from the specified donor.
     */
    List<BloodGroup> getCompatibleRecipientsForDonor(BloodGroup donor);

    /**
     * Generates a clear, clinically grounded explanation of the compatibility result.
     */
    String explainCompatibility(BloodGroup donor, BloodGroup recipient);

    default String getMedicalDisclaimer() {
        return MEDICAL_DISCLAIMER;
    }
}
