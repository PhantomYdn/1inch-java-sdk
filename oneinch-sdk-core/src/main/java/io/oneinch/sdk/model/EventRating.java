package io.oneinch.sdk.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum EventRating {
    RELIABLE("Reliable"),
    SCAM("Scam");

    private final String value;

    EventRating(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static EventRating fromValue(String value) {
        for (EventRating rating : EventRating.values()) {
            if (rating.value.equals(value)) {
                return rating;
            }
        }
        throw new IllegalArgumentException("Unknown EventRating: " + value);
    }

    @Override
    public String toString() {
        return value;
    }
}