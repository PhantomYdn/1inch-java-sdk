package io.oneinch.sdk.model.token;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenListRequest {
    
    private Integer chainId;
    private String provider;
    private String country;
}