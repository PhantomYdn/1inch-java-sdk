package io.oneinch.sdk.model.portfolio;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Portfolio API processing information
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PortfolioProcessingInfo {

    @JsonProperty("click_time")
    private Double clickTime;

    @JsonProperty("node_time")
    private Double nodeTime;

    @JsonProperty("microservices_time")
    private Double microservicesTime;

    @JsonProperty("redis_time")
    private Double redisTime;

    @JsonProperty("total_time")
    private Double totalTime;
}