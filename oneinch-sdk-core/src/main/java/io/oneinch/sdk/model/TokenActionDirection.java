package io.oneinch.sdk.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TokenActionDirection {
    IN("In"),
    OUT("Out"),
    SELF("Self"),
    ON("On");

    private final String value;

    TokenActionDirection(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static TokenActionDirection fromValue(String value) {
        for (TokenActionDirection direction : TokenActionDirection.values()) {
            if (direction.value.equals(value)) {
                return direction;
            }
        }
        throw new IllegalArgumentException("Unknown TokenActionDirection: " + value);
    }

    @Override
    public String toString() {
        return value;
    }
}