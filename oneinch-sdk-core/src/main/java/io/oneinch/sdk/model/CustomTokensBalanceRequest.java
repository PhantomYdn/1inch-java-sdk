package io.oneinch.sdk.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Internal request model for custom tokens POST request body
 * Maps to CustomTokensRequest schema in Balance API swagger
 */
@Data
@Builder
public class CustomTokensBalanceRequest {
    
    /**
     * List of custom token addresses
     */
    private List<String> tokens;
}