package com.bloodlink.service;

import com.bloodlink.model.Donor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Service evaluating donor rest interval requirements.
 * Enforces the configurable minimum donation interval (standard: 90 days).
 *
 * Notice: Meeting interval eligibility is a necessary prerequisite but does NOT replace
 * in-person clinical assessment, hemoglobin screening, or medical approval.
 */
@Service
public class DonorEligibilityService {

    public static final String ELIGIBILITY_DISCLAIMER = "Notice: Rest interval calculation is an administrative tool. " +
            "Clinical eligibility requires formal hemoglobin testing, physical examination, and medical clearance by authorized healthcare personnel.";

    @Value("${bloodlink.donor.min-donation-interval-days:90}")
    private int minDonationIntervalDays;

    public DonorEligibilityService() {
        this.minDonationIntervalDays = 90;
    }

    public DonorEligibilityService(int minDonationIntervalDays) {
        this.minDonationIntervalDays = minDonationIntervalDays;
    }

    public int getMinDonationIntervalDays() {
        return minDonationIntervalDays;
    }

    public void setMinDonationIntervalDays(int minDonationIntervalDays) {
        this.minDonationIntervalDays = minDonationIntervalDays;
    }

    /**
     * Calculates whether a donor has satisfied the minimum donation rest period as of a specific date.
     *
     * @param lastDonation date of previous whole blood donation (null if first-time donor)
     * @param asOfDate reference evaluation date (typically today)
     * @return true if rest interval is satisfied, false otherwise
     */
    public boolean isIntervalSatisfied(LocalDate lastDonation, LocalDate asOfDate) {
        if (lastDonation == null) {
            return true; // First-time or unrecorded donation
        }
        if (asOfDate == null) {
            asOfDate = LocalDate.now();
        }
        long daysBetween = ChronoUnit.DAYS.between(lastDonation, asOfDate);
        return daysBetween >= minDonationIntervalDays;
    }

    public boolean isDonorEligible(Donor donor, LocalDate asOfDate) {
        if (donor == null) return false;
        return isIntervalSatisfied(donor.getLastDonation(), asOfDate);
    }

    /**
     * Calculates the projected date when the donor will become eligible to donate again.
     */
    public LocalDate calculateEligibleAfterDate(LocalDate lastDonation) {
        if (lastDonation == null) {
            return LocalDate.now();
        }
        return lastDonation.plusDays(minDonationIntervalDays);
    }

    /**
     * Calculates the number of days remaining until the donor satisfies the donation interval.
     * Returns 0 if already satisfied or never donated.
     */
    public long getDaysRemaining(LocalDate lastDonation, LocalDate asOfDate) {
        if (lastDonation == null) {
            return 0;
        }
        if (asOfDate == null) {
            asOfDate = LocalDate.now();
        }
        LocalDate eligibleDate = calculateEligibleAfterDate(lastDonation);
        long remaining = ChronoUnit.DAYS.between(asOfDate, eligibleDate);
        return Math.max(0, remaining);
    }

    public String getEligibilityDisclaimer() {
        return ELIGIBILITY_DISCLAIMER;
    }
}
