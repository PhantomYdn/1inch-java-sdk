package io.oneinch.sdk.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class TokenListPriceChangeRequest {
    
    private Integer chainId;
    
    @JsonProperty("tokenAddresses")
    private List<String> tokenAddresses;
    
    @JsonProperty("interval")
    private String interval;
}