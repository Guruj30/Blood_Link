package com.bloodlink.exception;

/**
 * Thrown when an invalid transition, operation, or request payload is processed.
 */
public class InvalidOperationException extends BloodLinkException {

    public InvalidOperationException(String message) {
        super(message);
    }
}
