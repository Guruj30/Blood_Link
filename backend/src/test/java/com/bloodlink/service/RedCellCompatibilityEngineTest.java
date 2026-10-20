package com.bloodlink.service;

import com.bloodlink.model.BloodGroup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Red Cell Compatibility Engine Tests")
class RedCellCompatibilityEngineTest {

    private RedCellCompatibilityEngine engine;

    @BeforeEach
    void setUp() {
        engine = new RedCellCompatibilityEngine();
    }

    @ParameterizedTest
    @EnumSource(BloodGroup.class)
    @DisplayName("Universal Donor (O-) must be compatible with all 8 blood groups")
    void testUniversalDonorO_Negative(BloodGroup recipient) {
        assertTrue(engine.isCompatible(BloodGroup.O_NEGATIVE, recipient),
                "O- must be compatible with recipient: " + recipient.getCode());
    }

    @ParameterizedTest
    @EnumSource(BloodGroup.class)
    @DisplayName("Universal Recipient (AB+) can receive from all 8 blood groups")
    void testUniversalRecipientAB_Positive(BloodGroup donor) {
        assertTrue(engine.isCompatible(donor, BloodGroup.AB_POSITIVE),
                "AB+ must be able to receive from donor: " + donor.getCode());
    }

    @Test
    @DisplayName("Rh- recipient cannot receive Rh+ red cells")
    void testRhIncompatibility() {
        assertFalse(engine.isCompatible(BloodGroup.O_POSITIVE, BloodGroup.O_NEGATIVE),
                "O+ red cells must not be given to O- recipient");
        assertFalse(engine.isCompatible(BloodGroup.A_POSITIVE, BloodGroup.A_NEGATIVE),
                "A+ red cells must not be given to A- recipient");
        assertFalse(engine.isCompatible(BloodGroup.B_POSITIVE, BloodGroup.B_NEGATIVE),
                "B+ red cells must not be given to B- recipient");
        assertFalse(engine.isCompatible(BloodGroup.AB_POSITIVE, BloodGroup.AB_NEGATIVE),
                "AB+ red cells must not be given to AB- recipient");
    }

    @Test
    @DisplayName("ABO incompatibilities are strictly rejected")
    void testAboIncompatibilities() {
        assertFalse(engine.isCompatible(BloodGroup.A_POSITIVE, BloodGroup.B_POSITIVE),
                "A+ cannot donate red cells to B+");
        assertFalse(engine.isCompatible(BloodGroup.B_POSITIVE, BloodGroup.A_POSITIVE),
                "B+ cannot donate red cells to A+");
        assertFalse(engine.isCompatible(BloodGroup.AB_POSITIVE, BloodGroup.O_POSITIVE),
                "AB+ cannot donate red cells to O+");
        assertFalse(engine.isCompatible(BloodGroup.AB_NEGATIVE, BloodGroup.O_NEGATIVE),
                "AB- cannot donate red cells to O-");
    }

    @Test
    @DisplayName("Identical blood groups are always compatible")
    void testSelfCompatibility() {
        for (BloodGroup bg : BloodGroup.values()) {
            assertTrue(engine.isCompatible(bg, bg),
                    bg.getCode() + " must be compatible with itself");
        }
    }

    @Test
    @DisplayName("Recipient O- can only receive from O-")
    void testONegativeCompatibleDonors() {
        List<BloodGroup> donors = engine.getCompatibleDonorsForRecipient(BloodGroup.O_NEGATIVE);
        assertEquals(1, donors.size());
        assertEquals(BloodGroup.O_NEGATIVE, donors.get(0));
    }

    @Test
    @DisplayName("Null inputs are safely rejected without NullPointerException")
    void testNullSafety() {
        assertFalse(engine.isCompatible(null, BloodGroup.A_POSITIVE));
        assertFalse(engine.isCompatible(BloodGroup.A_POSITIVE, null));
        assertFalse(engine.isCompatible(null, null));
    }

    @Test
    @DisplayName("Medical disclaimer is present and non-empty")
    void testMedicalDisclaimerPresent() {
        String disclaimer = engine.getMedicalDisclaimer();
        assertNotNull(disclaimer);
        assertTrue(disclaimer.contains("NOT replace clinical crossmatching"));
    }
}
