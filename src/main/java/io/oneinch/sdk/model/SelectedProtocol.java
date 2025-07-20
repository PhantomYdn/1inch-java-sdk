package io.oneinch.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SelectedProtocol {
    
    @JsonProperty("name")
    private String name;
    
    @JsonProperty("part")
    private Double part;
    
    @JsonProperty("fromTokenAddress")
    private String fromTokenAddress;
    
    @JsonProperty("toTokenAddress")
    private String toTokenAddress;
}