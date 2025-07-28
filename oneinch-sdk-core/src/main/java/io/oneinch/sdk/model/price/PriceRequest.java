package io.oneinch.sdk.model.price;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Request model for getting token prices
 */
@Data
@Builder
public class PriceRequest {
    
    /**
     * Blockchain network identifier (1 for Ethereum, 137 for Polygon, etc.)
     */
    private Integer chainId;
    
    /**
     * List of token contract addresses to get prices for.
     * If null or empty, prices for all whitelisted tokens will be returned.
     */
    private List<String> addresses;
    
    /**
     * Currency to return prices in.
     * If null, prices are returned in native Wei.
     */
    private Currency currency;
}