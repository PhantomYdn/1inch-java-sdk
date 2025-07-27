package io.oneinch.sdk.model.tokendetails;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TokenPriceChangeResponse {
    
    @JsonProperty("inUSD")
    private Double inUSD;
    
    @JsonProperty("inPercent")
    private Double inPercent;
}