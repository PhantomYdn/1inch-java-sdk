package io.oneinch.sdk.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum HistoryEventType {
    TRANSACTION("Transaction"),
    LIMIT_ORDER("LimitOrder"),
    FUSION_SWAP("FusionSwap");

    private final String value;

    HistoryEventType(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static HistoryEventType fromValue(String value) {
        for (HistoryEventType type : HistoryEventType.values()) {
            if (type.value.equals(value)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown HistoryEventType: " + value);
    }

    @Override
    public String toString() {
        return value;
    }
}