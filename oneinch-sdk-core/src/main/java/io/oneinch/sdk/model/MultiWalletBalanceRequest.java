package io.oneinch.sdk.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Request for getting balances of custom tokens for multiple wallets
 */
@Data
@Builder
public class MultiWalletBalanceRequest {
    
    /**
     * Chain ID (e.g., 1 for Ethereum, 137 for Polygon)
     */
    private Integer chainId;
    
    /**
     * List of wallet addresses to get balances for
     */
    private List<String> wallets;
    
    /**
     * List of token addresses to get balances for
     */
    private List<String> tokens;
}