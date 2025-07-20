package io.oneinch.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ApproveCallDataResponse {
    
    @JsonProperty("data")
    private String data;
    
    @JsonProperty("gasPrice")
    private String gasPrice;
    
    @JsonProperty("to")
    private String to;
    
    @JsonProperty("value")
    private String value;
}