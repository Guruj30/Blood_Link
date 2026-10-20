package com.bloodlink.service;

import com.bloodlink.model.BloodGroup;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Concrete implementation of CompatibilityRuleEngine for Packed Red Blood Cells (PRBC).
 * Implements classical Landsteiner ABO and Rhesus antigen compatibility rules:
 * - O- is universal red-cell donor.
 * - AB+ is universal red-cell recipient.
 * - Rh- recipients must strictly receive Rh- red cells to prevent alloimmunization.
 */
@Service
@Primary
public class RedCellCompatibilityEngine implements CompatibilityRuleEngine {

    @Override
    public String getComponentType() {
        return "RED_CELL";
    }

    @Override
    public boolean isCompatible(BloodGroup donor, BloodGroup recipient) {
        if (donor == null || recipient == null) {
            return false;
        }

        // Rh compatibility: Rh- can donate to Rh+ and Rh-; Rh+ cannot donate to Rh-
        if (donor.isRhPositive() && !recipient.isRhPositive()) {
            return false;
        }

        // ABO compatibility for red blood cells
        String dType = donor.getAboType();
        String rType = recipient.getAboType();

        // O donor red cells have neither A nor B antigen -> compatible with all ABO types
        if ("O".equals(dType)) {
            return true;
        }
        // Identical ABO type is always compatible
        if (dType.equals(rType)) {
            return true;
        }
        // AB recipient red cells have neither anti-A nor anti-B antibodies -> can receive A or B
        if ("AB".equals(rType)) {
            return true;
        }

        return false;
    }

    @Override
    public List<BloodGroup> getCompatibleDonorsForRecipient(BloodGroup recipient) {
        if (recipient == null) return List.of();
        return Arrays.stream(BloodGroup.values())
                .filter(donor -> isCompatible(donor, recipient))
                .collect(Collectors.toList());
    }

    @Override
    public List<BloodGroup> getCompatibleRecipientsForDonor(BloodGroup donor) {
        if (donor == null) return List.of();
        return Arrays.stream(BloodGroup.values())
                .filter(recipient -> isCompatible(donor, recipient))
                .collect(Collectors.toList());
    }

    @Override
    public String explainCompatibility(BloodGroup donor, BloodGroup recipient) {
        if (donor == null || recipient == null) {
            return "Incomplete blood group specification.";
        }
        boolean compatible = isCompatible(donor, recipient);
        if (compatible) {
            if (donor == BloodGroup.O_NEGATIVE) {
                return String.format("COMPATIBLE: Donor group O- is a universal red blood cell donor (lacks A, B, and Rh antigens). Safe for recipient %s under emergency red cell protocol.", recipient.getCode());
            }
            if (recipient == BloodGroup.AB_POSITIVE) {
                return String.format("COMPATIBLE: Recipient group AB+ is a universal red cell recipient (serum lacks anti-A, anti-B, and anti-Rh antibodies). Compatible with donor %s.", donor.getCode());
            }
            return String.format("COMPATIBLE: Donor red blood cells %s are immunologically compatible with recipient %s.", donor.getCode(), recipient.getCode());
        } else {
            if (donor.isRhPositive() && !recipient.isRhPositive()) {
                return String.format("INCOMPATIBLE: Donor %s has Rh-D surface antigen while recipient %s is Rh-negative. Red cell transfusion would risk Rh isoimmunization / hemolytic reaction.", donor.getCode(), recipient.getCode());
            }
            return String.format("INCOMPATIBLE: Recipient %s serum contains antibodies against donor %s ABO red cell antigens. Transfusion causes acute hemolytic reaction.", recipient.getCode(), donor.getCode());
        }
    }
}
