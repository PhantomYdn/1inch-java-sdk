package io.oneinch.sdk.model.balance;

import lombok.Data;

import java.math.BigInteger;

/**
 * Balance and allowance item for a specific token
 */
@Data
public class BalanceAndAllowanceItem {
    
    /**
     * Token balance in wei (high precision)
     */
    private BigInteger balance;
    
    /**
     * Token allowance in wei (high precision)
     */
    private BigInteger allowance;
}