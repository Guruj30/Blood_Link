package com.bloodlink.exception;

/**
 * Thrown when an invalid or unrecognized blood group string is supplied.
 */
public class InvalidBloodGroupException extends BloodLinkException {

    public InvalidBloodGroupException(String bloodGroup) {
        super("Invalid blood group: '" + bloodGroup + "'. Valid blood groups are: A+, A-, B+, B-, AB+, AB-, O+, O-");
    }
}
