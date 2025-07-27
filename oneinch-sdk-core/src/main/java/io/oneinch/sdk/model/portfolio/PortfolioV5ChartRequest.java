package io.oneinch.sdk.model.portfolio;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Request model for Portfolio API v5 chart endpoints
 */
@Data
@Builder
public class PortfolioV5ChartRequest {
    
    private final List<String> addresses;
    private final Integer chainId;
    
    @Builder.Default
    private final String timerange = "1year";
    
    @Builder.Default
    private final Boolean useCache = false;
}