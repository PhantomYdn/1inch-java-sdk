package io.oneinch.sdk.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Request for getting custom token balances for a wallet address
 */
@Data
@Builder
public class CustomBalanceRequest {
    
    /**
     * Chain ID (e.g., 1 for Ethereum, 137 for Polygon)
     */
    private Integer chainId;
    
    /**
     * Wallet address to get balances for
     */
    private String walletAddress;
    
    /**
     * List of custom token addresses to get balances for
     */
    private List<String> tokens;
}