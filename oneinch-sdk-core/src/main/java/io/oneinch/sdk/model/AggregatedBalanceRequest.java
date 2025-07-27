package io.oneinch.sdk.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Request for getting aggregated balances and allowances for multiple wallets
 */
@Data
@Builder
public class AggregatedBalanceRequest {
    
    /**
     * Chain ID (e.g., 1 for Ethereum, 137 for Polygon)
     */
    private Integer chainId;
    
    /**
     * Spender address (e.g., DEX router address)
     */
    private String spender;
    
    /**
     * List of wallet addresses to get balances and allowances for
     */
    private List<String> wallets;
    
    /**
     * Filter out empty balances and allowances
     */
    private Boolean filterEmpty;
}