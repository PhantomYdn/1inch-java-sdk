package io.oneinch.sdk.model;

import lombok.Data;

/**
 * Balance and allowance item for a specific token
 */
@Data
public class BalanceAndAllowanceItem {
    
    /**
     * Token balance as string (in wei)
     */
    private String balance;
    
    /**
     * Token allowance as string (in wei)
     */
    private String allowance;
}