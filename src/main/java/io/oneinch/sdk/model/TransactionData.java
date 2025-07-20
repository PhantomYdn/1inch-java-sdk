package io.oneinch.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TransactionData {
    
    @JsonProperty("from")
    private String from;
    
    @JsonProperty("to")
    private String to;
    
    @JsonProperty("data")
    private String data;
    
    @JsonProperty("value")
    private String value;
    
    @JsonProperty("gasPrice")
    private String gasPrice;
    
    @JsonProperty("gas")
    private Long gas;
}