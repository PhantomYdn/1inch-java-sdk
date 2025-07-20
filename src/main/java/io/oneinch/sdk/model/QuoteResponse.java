package io.oneinch.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class QuoteResponse {
    
    @JsonProperty("srcToken")
    private TokenInfo srcToken;
    
    @JsonProperty("dstToken")
    private TokenInfo dstToken;
    
    @JsonProperty("dstAmount")
    private String dstAmount;
    
    @JsonProperty("protocols")
    private List<List<List<SelectedProtocol>>> protocols;
    
    @JsonProperty("gas")
    private Long gas;
}