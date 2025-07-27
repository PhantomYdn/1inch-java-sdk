package io.oneinch.sdk.model.tokendetails;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TokenListPriceChangeResponse {
    
    @JsonProperty("tokenAddress")
    private String tokenAddress;
    
    @JsonProperty("inUSD")
    private Double inUSD;
    
    @JsonProperty("inPercent")
    private Double inPercent;
}