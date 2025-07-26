package io.oneinch.sdk.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
 * Portfolio API value chart response
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PortfolioValueChartResponse {

    @JsonProperty("result")
    private List<AssetValueChartItem> result;

    @JsonProperty("meta")
    private PortfolioValueChartMeta meta;
}