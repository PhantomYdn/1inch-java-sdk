package io.oneinch.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Processing information metadata
 */
@Data
public class ProcessingInfo {
    
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