package io.oneinch.sdk.model.fusion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request for getting Fusion order history by maker address.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FusionOrderHistoryRequest {
    
    /**
     * Chain ID for the network.
     */
    private Integer chainId;
    
    /**
     * Maker's address.
     * Example: 0x1000000000000000000000000000000000000001
     */
    private String address;
    
    /**
     * Pagination step, default: 1 (page = offset / limit).
     */
    private Integer page;
    
    /**
     * Number of orders to receive (default: 100, max: 500).
     */
    private Integer limit;
    
    /**
     * Timestamp from in milliseconds for interval [timestampFrom, timestampTo).
     * Example: 1750849584021
     */
    private Long timestampFrom;
    
    /**
     * Timestamp to in milliseconds for interval [timestampFrom, timestampTo).
     * Example: 1750849584021
     */
    private Long timestampTo;
    
    /**
     * Find history by the given maker token.
     * Example: 0xc02aaa39b223fe8d0a0e5c4f27ead9083c756cc2
     */
    private String makerToken;
    
    /**
     * Find history by the given taker token.
     * Example: 0xc2132d05d31c914a87c6611c10748aeb04b58e8f
     */
    private String takerToken;
    
    /**
     * Find history items by source or destination token.
     * Example: 0xc2132d05d31c914a87c6611c10748aeb04b58e8f
     */
    private String withToken;
    
    /**
     * Settlement extension version: 2.0 or 2.1. By default: all.
     * Example: "2.0"
     */
    private String version;
}