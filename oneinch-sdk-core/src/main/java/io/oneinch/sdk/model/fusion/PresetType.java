package io.oneinch.sdk.model.fusion;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enumeration of available preset types for Fusion orders.
 */
public enum PresetType {
    FAST("fast"),
    MEDIUM("medium"),
    SLOW("slow"),
    CUSTOM("custom");
    
    private final String value;
    
    PresetType(String value) {
        this.value = value;
    }
    
    @JsonValue
    public String getValue() {
        return value;
    }
    
    @JsonCreator
    public static PresetType fromValue(String value) {
        for (PresetType preset : PresetType.values()) {
            if (preset.value.equals(value)) {
                return preset;
            }
        }
        throw new IllegalArgumentException("Unknown preset type: " + value);
    }
    
    @Override
    public String toString() {
        return value;
    }
}