package com.bloodlink.service;

import com.bloodlink.model.BloodGroup;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Concrete implementation of CompatibilityRuleEngine for Fresh Frozen Plasma (FFP).
 * Demonstrates OOP Polymorphism alongside RedCellCompatibilityEngine.
 *
 * In plasma transfusion, rules invert from red cells because plasma contains antibodies:
 * - Group AB plasma contains NO anti-A or anti-B antibodies -> Universal Plasma Donor.
 * - Group O plasma contains BOTH anti-A and anti-B antibodies -> Can only donate plasma to O recipients.
 */
@Service
public class PlasmaCompatibilityEngine implements CompatibilityRuleEngine {

    @Override
    public String getComponentType() {
        return "PLASMA";
    }

    @Override
    public boolean isCompatible(BloodGroup donor, BloodGroup recipient) {
        if (donor == null || recipient == null) {
            return false;
        }

        String dType = donor.getAboType();
        String rType = recipient.getAboType();

        // In plasma, AB donor plasma has no ABO antibodies -> universal plasma donor
        if ("AB".equals(dType)) {
            return true;
        }
        // Identical ABO types are compatible
        if (dType.equals(rType)) {
            return true;
        }
        // Group A plasma (contains anti-B) compatible with A and O recipients
        if ("A".equals(dType) && ("A".equals(rType) || "O".equals(rType))) {
            return true;
        }
        // Group B plasma (contains anti-A) compatible with B and O recipients
        if ("B".equals(dType) && ("B".equals(rType) || "O".equals(rType))) {
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
            if ("AB".equals(donor.getAboType())) {
                return String.format("COMPATIBLE: Donor group %s is an AB universal plasma donor (plasma lacks anti-A and anti-B antibodies). Safe for %s plasma transfusion.", donor.getCode(), recipient.getCode());
            }
            return String.format("COMPATIBLE: Donor %s plasma antibodies are compatible with recipient %s red cells.", donor.getCode(), recipient.getCode());
        } else {
            return String.format("INCOMPATIBLE: Donor %s plasma contains antibodies that would hemolyze recipient %s red cells.", donor.getCode(), recipient.getCode());
        }
    }
}
