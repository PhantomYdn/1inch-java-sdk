package io.oneinch.sdk.model.fusion;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Enumeration of possible Fusion order statuses.
 */
public enum OrderStatus {
    PENDING("pending"),
    FILLED("filled"),
    FALSE_PREDICATE("false-predicate"),
    NOT_ENOUGH_BALANCE_OR_ALLOWANCE("not-enough-balance-or-allowance"),
    EXPIRED("expired"),
    PARTIALLY_FILLED("partially-filled"),
    WRONG_PERMIT("wrong-permit"),
    CANCELLED("cancelled"),
    INVALID_SIGNATURE("invalid-signature"),
    INVALID_MAKER_TRAITS("invalid-maker-traits"),
    AWAITING_SIGNATURES("awaiting-signatures");
    
    private final String value;
    
    OrderStatus(String value) {
        this.value = value;
    }
    
    @JsonValue
    public String getValue() {
        return value;
    }
    
    @JsonCreator
    public static OrderStatus fromValue(String value) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown order status: " + value);
    }
    
    @Override
    public String toString() {
        return value;
    }
}