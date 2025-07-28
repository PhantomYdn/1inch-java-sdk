package io.oneinch.sdk.model.fusion;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request for getting active Fusion orders.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FusionActiveOrdersRequest {
    
    /**
     * Chain ID for the network.
     */
    private Integer chainId;
    
    /**
     * Pagination step, default: 1 (page = offset / limit).
     */
    private Integer page;
    
    /**
     * Number of active orders to receive (default: 100, max: 500).
     */
    private Integer limit;
    
    /**
     * Settlement extension version: 2.0 or 2.1. By default: all.
     * Example: "2.0"
     */
    private String version;
}