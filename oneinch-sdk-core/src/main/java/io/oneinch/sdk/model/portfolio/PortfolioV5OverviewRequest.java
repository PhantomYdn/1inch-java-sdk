package io.oneinch.sdk.model.portfolio;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Request model for Portfolio API v5 overview endpoints
 */
@Data
@Builder
public class PortfolioV5OverviewRequest {
    
    private final List<String> addresses;
    private final Integer chainId;
    
    @Builder.Default
    private final Boolean useCache = false;
}