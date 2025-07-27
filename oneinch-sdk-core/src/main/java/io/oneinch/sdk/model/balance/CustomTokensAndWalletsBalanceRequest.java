package io.oneinch.sdk.model.balance;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Internal request model for custom tokens and wallets POST request body
 * Maps to CustomTokensAndWalletsRequest schema in Balance API swagger
 */
@Data
@Builder
public class CustomTokensAndWalletsBalanceRequest {
    
    /**
     * List of custom token addresses
     */
    private List<String> tokens;
    
    /**
     * List of wallet addresses
     */
    private List<String> wallets;
}