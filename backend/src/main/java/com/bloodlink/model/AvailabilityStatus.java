package com.bloodlink.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Operational demo availability for a blood donor in the registry.
 * Notice: This is an administrative contact/presence status and does NOT imply formal medical eligibility.
 */
public enum AvailabilityStatus {
    AVAILABLE("available"),
    UNAVAILABLE("unavailable"),
    ON_LEAVE("on_leave");

    private final String value;

    AvailabilityStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static AvailabilityStatus fromValue(String value) {
        if (value == null) return AVAILABLE;
        for (AvailabilityStatus s : values()) {
            if (s.value.equalsIgnoreCase(value.trim())) {
                return s;
            }
        }
        return AVAILABLE;
    }
}
