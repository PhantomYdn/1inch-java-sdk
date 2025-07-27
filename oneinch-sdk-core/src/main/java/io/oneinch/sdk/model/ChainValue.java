package io.oneinch.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Value breakdown by blockchain
 */
@Data
public class ChainValue {
    
    /**
     * USD value of assets on this chain
     */
    @JsonProperty("value_usd")
    private Double valueUsd;
    
    /**
     * Chain identifier
     */
    @JsonProperty("chain_id")
    private Integer chainId;
    
    /**
     * Human-readable chain name
     */
    @JsonProperty("chain_name")
    private String chainName;
}