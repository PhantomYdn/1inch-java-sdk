package io.oneinch.sdk.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * Request for Portfolio API details endpoints (protocols/tokens details)
 */
@Data
@Builder
public class PortfolioDetailsRequest {

    /**
     * Array of Ethereum addresses to analyze (required)
     * Each address must be 42 characters long (0x + 40 hex chars)
     */
    private final List<String> addresses;

    /**
     * Chain ID to filter results (optional)
     * If null, returns data for all supported chains
     */
    private final Integer chainId;

    /**
     * Time range for analysis (optional, default: 1year)
     * Valid values: 1day, 1week, 1month, 1year, 3years
     */
    @Builder.Default
    private final String timerange = "1year";

    /**
     * Whether to include closed positions (optional, default: true)
     */
    @Builder.Default
    private final Boolean closed = true;

    /**
     * Closed positions threshold value (optional, default: 1.0)
     */
    @Builder.Default
    private final Double closedThreshold = 1.0;

    /**
     * Whether to use cached response (optional, default: false)
     */
    @Builder.Default
    private final Boolean useCache = false;
}