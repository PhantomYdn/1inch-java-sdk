package io.oneinch.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Token balance information
 */
@Data
public class TokenBalance {
    
    private Integer chain;
    private String address;
    private Integer decimals;
    private String symbol;
    private String name;
    private Double amount;
    
    @JsonProperty("price_usd")
    private Double priceUsd;
}