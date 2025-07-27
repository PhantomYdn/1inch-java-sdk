package io.oneinch.sdk.model.balance;

import lombok.Builder;
import lombok.Data;

/**
 * Request for getting token balances for a wallet address
 */
@Data
@Builder
public class BalanceRequest {
    
    /**
     * Chain ID (e.g., 1 for Ethereum, 137 for Polygon)
     */
    private Integer chainId;
    
    /**
     * Wallet address to get balances for
     */
    private String walletAddress;
}