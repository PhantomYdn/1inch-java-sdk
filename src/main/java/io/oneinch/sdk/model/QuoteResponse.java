package io.oneinch.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigInteger;
import java.util.List;

@Data
public class QuoteResponse {
    
    @JsonProperty("srcToken")
    private Token srcToken;
    
    @JsonProperty("dstToken")
    private Token dstToken;
    
    @JsonProperty("dstAmount")
    private BigInteger dstAmount;
    
    @JsonProperty("protocols")
    private List<List<List<SelectedProtocol>>> protocols;
    
    @JsonProperty("gas")
    private BigInteger gas;
}