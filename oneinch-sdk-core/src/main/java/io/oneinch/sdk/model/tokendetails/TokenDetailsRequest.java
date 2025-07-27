package io.oneinch.sdk.model.tokendetails;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenDetailsRequest {
    
    private Integer chainId;
    private String contractAddress;
    private String provider;
}