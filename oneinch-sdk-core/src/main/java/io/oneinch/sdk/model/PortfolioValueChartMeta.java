package io.oneinch.sdk.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Portfolio API value chart response metadata
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PortfolioValueChartMeta {

    @JsonProperty("cached_at")
    private Long cachedAt;

    @JsonProperty("system")
    private PortfolioProcessingInfo system;

    @JsonProperty("data_issues")
    private Object dataIssues; // Keeping as Object due to complex nested structure
}