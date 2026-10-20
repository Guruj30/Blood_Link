package com.bloodlink.exception;

/**
 * Base unchecked exception for all BloodLink domain and business rule violations.
 */
public abstract class BloodLinkException extends RuntimeException {

    protected BloodLinkException(String message) {
        super(message);
    }

    protected BloodLinkException(String message, Throwable cause) {
        super(message, cause);
    }
}
