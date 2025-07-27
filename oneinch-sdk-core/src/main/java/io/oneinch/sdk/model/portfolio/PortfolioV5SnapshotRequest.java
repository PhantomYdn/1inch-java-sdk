package io.oneinch.sdk.model.portfolio;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Request model for Portfolio API v5 snapshot endpoints
 */
@Data
@Builder
public class PortfolioV5SnapshotRequest {
    
    private final List<String> addresses;
    private final Integer chainId;
    private final Long timestamp;
    
    @Builder.Default
    private final Boolean useCache = false;
}