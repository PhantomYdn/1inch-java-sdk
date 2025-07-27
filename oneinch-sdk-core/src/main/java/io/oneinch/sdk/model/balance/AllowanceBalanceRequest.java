package io.oneinch.sdk.model.balance;

import lombok.Builder;
import lombok.Data;

/**
 * Request for getting token allowances for a spender and wallet address
 */
@Data
@Builder
public class AllowanceBalanceRequest {
    
    /**
     * Chain ID (e.g., 1 for Ethereum, 137 for Polygon)
     */
    private Integer chainId;
    
    /**
     * Spender address (e.g., DEX router address)
     */
    private String spender;
    
    /**
     * Wallet address to get allowances for
     */
    private String walletAddress;
}