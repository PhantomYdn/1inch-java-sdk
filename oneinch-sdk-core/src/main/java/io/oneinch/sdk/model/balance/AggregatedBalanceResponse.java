package io.oneinch.sdk.model.balance;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Aggregated balances and allowances response for multiple wallets
 * Maps to AggregatedBalancesAndAllowancesResponse schema in Balance API swagger
 */
@Data
public class AggregatedBalanceResponse {
    
    /**
     * Token decimals
     */
    private Integer decimals;
    
    /**
     * Token symbol
     */
    private String symbol;
    
    /**
     * Token tags
     */
    private List<String> tags;
    
    /**
     * Token contract address
     */
    private String address;
    
    /**
     * Token name
     */
    private String name;
    
    /**
     * Token logo URI
     */
    private String logoURI;
    
    /**
     * Whether this is a custom token
     */
    private Boolean isCustom;
    
    /**
     * Wallet-specific balance and allowance data
     * Map of wallet address to balance/allowance information
     */
    private Map<String, Object> wallets;
    
    /**
     * Token type
     */
    private String type;
    
    /**
     * Whether the token is tracked
     */
    private Boolean tracked;
}