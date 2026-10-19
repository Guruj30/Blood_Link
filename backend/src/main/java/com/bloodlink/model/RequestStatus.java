package com.bloodlink.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Lifecycle status of an emergency request in the clinical dispatch pipeline.
 * Pending -> Reviewing -> In Progress -> Fulfilled or Cancelled.
 */
public enum RequestStatus {
    PENDING("pending"),
    REVIEWING("reviewing"),
    IN_PROGRESS("in_progress"),
    FULFILLED("fulfilled"),
    CANCELLED("cancelled");

    private final String value;

    RequestStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static RequestStatus fromValue(String value) {
        if (value == null) return PENDING;
        for (RequestStatus s : values()) {
            if (s.value.equalsIgnoreCase(value.trim())) {
                return s;
            }
        }
        throw new IllegalArgumentException("Unknown request status: " + value);
    }
}
