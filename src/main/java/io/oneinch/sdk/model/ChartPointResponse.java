package io.oneinch.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ChartPointResponse {
    
    @JsonProperty("t")
    private Long time;
    
    @JsonProperty("v")
    private Double price;
}