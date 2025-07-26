package io.oneinch.sdk.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum TransactionStatus {
    COMPLETED("Completed"),
    FAILED("Failed"),
    PENDING("Pending"),
    CANCELLED("Cancelled");

    private final String value;

    TransactionStatus(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static TransactionStatus fromValue(String value) {
        for (TransactionStatus status : TransactionStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown TransactionStatus: " + value);
    }

    @Override
    public String toString() {
        return value;
    }
}