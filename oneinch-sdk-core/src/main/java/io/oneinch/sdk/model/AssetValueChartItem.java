package io.oneinch.sdk.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Portfolio API asset value chart item
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AssetValueChartItem {

    @JsonProperty("timestamp")
    private Long timestamp;

    @JsonProperty("value_usd")
    private Double valueUsd;
}