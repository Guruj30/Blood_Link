package com.bloodlink.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enumeration representing the 8 human ABO and Rh blood groups.
 * Provides ABO type and Rh antigen classification for immunological rules.
 */
public enum BloodGroup {
    A_POSITIVE("A+", "A", true),
    A_NEGATIVE("A-", "A", false),
    B_POSITIVE("B+", "B", true),
    B_NEGATIVE("B-", "B", false),
    AB_POSITIVE("AB+", "AB", true),
    AB_NEGATIVE("AB-", "AB", false),
    O_POSITIVE("O+", "O", true),
    O_NEGATIVE("O-", "O", false);

    private final String code;
    private final String aboType;
    private final boolean rhPositive;

    BloodGroup(String code, String aboType, boolean rhPositive) {
        this.code = code;
        this.aboType = aboType;
        this.rhPositive = rhPositive;
    }

    @JsonValue
    public String getCode() {
        return code;
    }

    public String getAboType() {
        return aboType;
    }

    public boolean isRhPositive() {
        return rhPositive;
    }

    @JsonCreator
    public static BloodGroup fromCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Blood group cannot be null or empty");
        }
        String normalized = code.trim().toUpperCase();
        for (BloodGroup bg : values()) {
            if (bg.code.equalsIgnoreCase(normalized)) {
                return bg;
            }
        }
        throw new IllegalArgumentException("Invalid blood group: " + code + ". Allowed values: A+, A-, B+, B-, AB+, AB-, O+, O-");
    }

    @Override
    public String toString() {
        return code;
    }
}
