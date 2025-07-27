package io.oneinch.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Value breakdown by address
 */
@Data
public class AddressValue {
    
    /**
     * USD value of assets in this address
     */
    @JsonProperty("value_usd")
    private Double valueUsd;
    
    /**
     * Address
     */
    private String address;
}