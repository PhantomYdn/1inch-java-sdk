package io.oneinch.sdk.model.portfolio;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Portfolio API response metadata
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PortfolioResponseMeta {

    @JsonProperty("cached_at")
    private Long cachedAt;

    @JsonProperty("system")
    private PortfolioProcessingInfo system;
}