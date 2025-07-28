package io.oneinch.sdk.model.price;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Request body for POST price endpoint
 */
@Data
@Builder
public class PostPriceRequest {
    
    /**
     * List of token contract addresses to get prices for
     */
    @JsonProperty("tokens")
    private List<String> tokens;
    
    /**
     * Currency to return prices in.
     * If no currency provided, then price returned in native Wei
     */
    @JsonProperty("currency")
    private String currency;
}