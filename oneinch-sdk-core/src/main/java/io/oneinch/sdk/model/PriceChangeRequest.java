package io.oneinch.sdk.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PriceChangeRequest {
    
    private Integer chainId;
    private String tokenAddress;
    private String interval;
}