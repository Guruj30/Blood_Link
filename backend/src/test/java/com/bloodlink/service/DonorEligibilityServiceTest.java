package com.bloodlink.service;

import com.bloodlink.model.AvailabilityStatus;
import com.bloodlink.model.BloodGroup;
import com.bloodlink.model.Donor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Donor Eligibility Service Tests")
class DonorEligibilityServiceTest {

    private DonorEligibilityService eligibilityService;
    private final LocalDate today = LocalDate.of(2026, 10, 19);

    @BeforeEach
    void setUp() {
        eligibilityService = new DonorEligibilityService(90);
    }

    @Test
    @DisplayName("First-time donor (null last donation) satisfies interval requirement")
    void testFirstTimeDonor() {
        assertTrue(eligibilityService.isIntervalSatisfied(null, today));
        assertEquals(0, eligibilityService.getDaysRemaining(null, today));
    }

    @Test
    @DisplayName("Boundary: Exactly 89 days since last donation is INELIGIBLE")
    void test89DaysSinceDonation() {
        LocalDate donationDate = today.minusDays(89);
        assertFalse(eligibilityService.isIntervalSatisfied(donationDate, today),
                "89 days is below 90-day interval requirement and must be ineligible");
        assertEquals(1, eligibilityService.getDaysRemaining(donationDate, today));
    }

    @Test
    @DisplayName("Boundary: Exactly 90 days since last donation is ELIGIBLE")
    void test90DaysSinceDonation() {
        LocalDate donationDate = today.minusDays(90);
        assertTrue(eligibilityService.isIntervalSatisfied(donationDate, today),
                "Exactly 90 days satisfies the interval requirement");
        assertEquals(0, eligibilityService.getDaysRemaining(donationDate, today));
    }

    @Test
    @DisplayName("Boundary: 91 days since last donation is ELIGIBLE")
    void test91DaysSinceDonation() {
        LocalDate donationDate = today.minusDays(91);
        assertTrue(eligibilityService.isIntervalSatisfied(donationDate, today),
                "91 days satisfies the interval requirement");
        assertEquals(0, eligibilityService.getDaysRemaining(donationDate, today));
    }

    @Test
    @DisplayName("Recent donation (e.g. 10 days ago) calculates correct days remaining")
    void testRecentDonationDaysRemaining() {
        LocalDate donationDate = today.minusDays(10);
        assertFalse(eligibilityService.isIntervalSatisfied(donationDate, today));
        assertEquals(80, eligibilityService.getDaysRemaining(donationDate, today));
    }

    @Test
    @DisplayName("Configurable interval can be adjusted (e.g. 120 days)")
    void testConfigurableInterval() {
        eligibilityService.setMinDonationIntervalDays(120);
        LocalDate donationDate = today.minusDays(100);

        assertFalse(eligibilityService.isIntervalSatisfied(donationDate, today),
                "100 days should be ineligible when minimum interval is set to 120");
        assertEquals(20, eligibilityService.getDaysRemaining(donationDate, today));
    }

    @Test
    @DisplayName("Donor object evaluation delegates properly")
    void testDonorObjectEvaluation() {
        Donor donor = new Donor("D-1", "Test Donor", BloodGroup.O_POSITIVE, "Chennai",
                "+91-99999-00000", AvailabilityStatus.AVAILABLE,
                today.minusDays(30), today.plusDays(60), today.minusDays(200), "Notes");

        assertFalse(eligibilityService.isDonorEligible(donor, today));
    }

    @Test
    @DisplayName("Medical disclaimer is present")
    void testDisclaimer() {
        assertNotNull(eligibilityService.getEligibilityDisclaimer());
        assertTrue(eligibilityService.getEligibilityDisclaimer().contains("Clinical eligibility requires formal hemoglobin testing"));
    }
}
