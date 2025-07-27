package io.oneinch.sdk.model.portfolio;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Request model for Portfolio API v5 metrics endpoints
 */
@Data
@Builder
public class PortfolioV5MetricsRequest {
    
    private final List<String> addresses;
    private final Integer chainId;
    private final String protocolGroupId;
    private final String contractAddress;
    private final Integer tokenId;
    private final String timerange;
    
    @Builder.Default
    private final Boolean useCache = false;
}