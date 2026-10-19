package com.bloodlink.exception;

/**
 * Thrown when a donor donation or registration violates eligibility rules (e.g., minimum rest interval).
 */
public class IneligibleDonorException extends BloodLinkException {

    public IneligibleDonorException(String message) {
        super(message);
    }
}
