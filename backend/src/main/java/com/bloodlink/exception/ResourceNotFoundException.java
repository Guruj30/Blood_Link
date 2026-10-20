package com.bloodlink.exception;

/**
 * Thrown when a requested resource (Donor, Request, Inventory group) is not found.
 */
public class ResourceNotFoundException extends BloodLinkException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String resourceType, String id) {
        super(resourceType + " not found with id: " + id);
    }
}
