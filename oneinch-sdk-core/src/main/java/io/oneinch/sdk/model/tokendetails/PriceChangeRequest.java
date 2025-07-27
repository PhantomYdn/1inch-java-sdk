package io.oneinch.sdk.model.tokendetails;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PriceChangeRequest {
    
    private Integer chainId;
    private String tokenAddress;
    private String interval;
}