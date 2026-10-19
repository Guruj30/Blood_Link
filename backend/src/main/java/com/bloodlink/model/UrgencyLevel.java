package com.bloodlink.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Urgency levels for emergency blood requests.
 * Priority weights define ordering: CRITICAL (1 - highest) > HIGH (2) > NORMAL (3) > LOW (4 - lowest).
 */
public enum UrgencyLevel {
    CRITICAL("critical", 1),
    HIGH("high", 2),
    NORMAL("normal", 3),
    LOW("low", 4);

    private final String value;
    private final int priorityWeight;

    UrgencyLevel(String value, int priorityWeight) {
        this.value = value;
        this.priorityWeight = priorityWeight;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    public int getPriorityWeight() {
        return priorityWeight;
    }

    @JsonCreator
    public static UrgencyLevel fromValue(String value) {
        if (value == null) return NORMAL;
        for (UrgencyLevel level : values()) {
            if (level.value.equalsIgnoreCase(value.trim())) {
                return level;
            }
        }
        return NORMAL;
    }
}
