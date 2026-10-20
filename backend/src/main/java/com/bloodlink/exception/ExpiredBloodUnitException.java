package com.bloodlink.exception;

/**
 * Thrown when an expired unit of blood is encountered during allocation or transfusion.
 */
public class ExpiredBloodUnitException extends BloodLinkException {

    public ExpiredBloodUnitException(String message) {
        super(message);
    }
}
