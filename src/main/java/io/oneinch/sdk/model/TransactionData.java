package io.oneinch.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigInteger;

@Data
public class TransactionData {
    
    @JsonProperty("from")
    private String from;
    
    @JsonProperty("to")
    private String to;
    
    @JsonProperty("data")
    private String data;
    
    @JsonProperty("value")
    private BigInteger value;
    
    @JsonProperty("gasPrice")
    private BigInteger gasPrice;
    
    @JsonProperty("gas")
    private BigInteger gas;
}