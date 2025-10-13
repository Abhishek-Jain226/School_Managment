package com.app.Enum;

import lombok.Getter;

@Getter
public enum TripType {
    MORNING_PICKUP("Morning Pickup"),
    AFTERNOON_DROP("Afternoon Drop"),
    SPECIAL_TRIP("Special Trip"),
    FIELD_TRIP("Field Trip");

    private final String displayName;

    TripType(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    // Helper method to get enum from display name
    public static TripType fromDisplayName(String displayName) {
        for (TripType type : TripType.values()) {
            if (type.displayName.equals(displayName)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No TripType found for display name: " + displayName);
    }

    // Helper method to get enum from enum name
    public static TripType fromEnumName(String enumName) {
        try {
            return TripType.valueOf(enumName.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("No TripType found for enum name: " + enumName);
        }
    }
}
